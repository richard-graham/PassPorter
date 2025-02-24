package com.example.passporter.presentation.feature.add.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passporter.domain.entity.OperatingHours
import com.example.passporter.domain.entity.SeasonalHours
import java.time.LocalDate

private val DAYS =
    listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
private val TIME_SLOTS = (0..23).flatMap { hour ->
    listOf("$hour:00", "$hour:30").map {
        if (hour < 10) "0$it" else it
    }
}

private val SEASONS = listOf(
    "regular" to "Regular Hours",
    "summer" to "Summer Season",
    "winter" to "Winter Season"
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOperatingHoursSelector(
    operatingHours: OperatingHours,
    onOperatingHoursChange: (OperatingHours) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSeason by remember { mutableStateOf("regular") }
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerType by remember { mutableStateOf<String?>(null) }
    var seasonExpanded by remember { mutableStateOf(false) }

    val schedule = remember(operatingHours) {
        when (selectedSeason) {
            "summer" -> operatingHours.summerHours?.schedule
            "winter" -> operatingHours.winterHours?.schedule
            else -> operatingHours.regular
        }?.let { parseScheduleString(it) } ?: DAYS.associateWith { DaySchedule() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Season selector dropdown
        ExposedDropdownMenuBox(
            expanded = seasonExpanded,
            onExpandedChange = { seasonExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = SEASONS.find { it.first == selectedSeason }?.second ?: "Regular Hours",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seasonExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = seasonExpanded,
                onDismissRequest = { seasonExpanded = false }
            ) {
                SEASONS.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedSeason = id
                            seasonExpanded = false
                        }
                    )
                }
            }
        }

        // Date range buttons for seasonal hours
        if (selectedSeason != "regular") {
            val seasonHours = when (selectedSeason) {
                "summer" -> operatingHours.summerHours
                "winter" -> operatingHours.winterHours
                else -> null
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        datePickerType = "start"
                        showDatePicker = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        formatDateForButton(
                            date = seasonHours?.startDate,
                            defaultText = "Set Start Date"
                        )
                    )
                }

                Button(
                    onClick = {
                        datePickerType = "end"
                        showDatePicker = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        formatDateForButton(
                            date = seasonHours?.endDate,
                            defaultText = "Set End Date"
                        )
                    )
                }
            }
        }

        // Days schedule
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DAYS.forEach { day ->
                DayScheduleCard(
                    day = day,
                    schedule = schedule[day] ?: DaySchedule(),
                    onScheduleChange = { newSchedule ->
                        val updatedSchedule = schedule.toMutableMap().apply {
                            put(day, newSchedule)
                        }
                        val formattedSchedule = formatScheduleToString(updatedSchedule)

                        when (selectedSeason) {
                            "summer" -> {
                                val currentSummerHours = operatingHours.summerHours
                                onOperatingHoursChange(
                                    operatingHours.copy(
                                        summerHours = currentSummerHours?.copy(
                                            schedule = formattedSchedule
                                        ) ?: SeasonalHours(
                                            schedule = formattedSchedule,
                                            startDate = LocalDate.now(),
                                            endDate = LocalDate.now().plusMonths(3)
                                        )
                                    )
                                )
                            }

                            "winter" -> {
                                val currentWinterHours = operatingHours.winterHours
                                onOperatingHoursChange(
                                    operatingHours.copy(
                                        winterHours = currentWinterHours?.copy(
                                            schedule = formattedSchedule
                                        ) ?: SeasonalHours(
                                            schedule = formattedSchedule,
                                            startDate = LocalDate.now(),
                                            endDate = LocalDate.now().plusMonths(3)
                                        )
                                    )
                                )
                            }

                            else -> onOperatingHoursChange(
                                operatingHours.copy(
                                    regular = formattedSchedule
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    if (showDatePicker && datePickerType != null) {
        val datePickerState = rememberDatePickerState()

        // Set initial date in the picker based on existing values
        if (selectedSeason == "summer" || selectedSeason == "winter") {
            val seasonHours = if (selectedSeason == "summer")
                operatingHours.summerHours else operatingHours.winterHours

            if (seasonHours != null) {
                val existingDate = if (datePickerType == "start")
                    seasonHours.startDate else seasonHours.endDate

                // We need to convert LocalDate to millis for the date picker
                val millis = existingDate.toEpochDay() * 24 * 60 * 60 * 1000
                datePickerState.selectedDateMillis = millis
            } else {
                // Set default to today if no date exists
                datePickerState.selectedDateMillis = LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000
            }
        } else {
            // Default to today if no season selected (this shouldn't happen but just in case)
            datePickerState.selectedDateMillis = LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000
        }

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                datePickerType = null
            },
            confirmButton = {
                TextButton(onClick = {
                    // Update the date for the selected season
                    val selectedDateMillis = datePickerState.selectedDateMillis ?:
                    (LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.ofEpochDay(selectedDateMillis / (24 * 60 * 60 * 1000))

                    when (selectedSeason) {
                        "summer" -> {
                            val currentSummerHours = operatingHours.summerHours
                            val updatedSummerHours = if (currentSummerHours != null) {
                                if (datePickerType == "start") {
                                    currentSummerHours.copy(startDate = selectedDate)
                                } else {
                                    currentSummerHours.copy(endDate = selectedDate)
                                }
                            } else {
                                // Create new summer hours if they don't exist
                                if (datePickerType == "start") {
                                    SeasonalHours(
                                        schedule = operatingHours.regular ?: "",
                                        startDate = selectedDate,
                                        endDate = selectedDate.plusMonths(3)
                                    )
                                } else {
                                    SeasonalHours(
                                        schedule = operatingHours.regular ?: "",
                                        startDate = selectedDate.minusMonths(3),
                                        endDate = selectedDate
                                    )
                                }
                            }
                            onOperatingHoursChange(operatingHours.copy(summerHours = updatedSummerHours))
                        }
                        "winter" -> {
                            val currentWinterHours = operatingHours.winterHours
                            val updatedWinterHours = if (currentWinterHours != null) {
                                if (datePickerType == "start") {
                                    currentWinterHours.copy(startDate = selectedDate)
                                } else {
                                    currentWinterHours.copy(endDate = selectedDate)
                                }
                            } else {
                                // Create new winter hours if they don't exist
                                if (datePickerType == "start") {
                                    SeasonalHours(
                                        schedule = operatingHours.regular ?: "",
                                        startDate = selectedDate,
                                        endDate = selectedDate.plusMonths(3)
                                    )
                                } else {
                                    SeasonalHours(
                                        schedule = operatingHours.regular ?: "",
                                        startDate = selectedDate.minusMonths(3),
                                        endDate = selectedDate
                                    )
                                }
                            }
                            onOperatingHoursChange(operatingHours.copy(winterHours = updatedWinterHours))
                        }
                    }
                    showDatePicker = false
                    datePickerType = null
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerType = null
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayScheduleCard(
    day: String,
    schedule: DaySchedule,
    onScheduleChange: (DaySchedule) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = schedule.isOpen,
                    onCheckedChange = { isOpen ->
                        onScheduleChange(schedule.copy(isOpen = isOpen))
                    }
                )
            }

            if (schedule.isOpen) {
                schedule.ranges.forEachIndexed { index, range ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimePickerDropdown(
                            label = "Start",
                            selected = range.start,
                            onTimeSelected = { newTime ->
                                val newRanges = schedule.ranges.toMutableList()
                                newRanges[index] = range.copy(start = newTime)
                                onScheduleChange(schedule.copy(ranges = newRanges))
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Text("to")

                        TimePickerDropdown(
                            label = "End",
                            selected = range.end,
                            onTimeSelected = { newTime ->
                                val newRanges = schedule.ranges.toMutableList()
                                newRanges[index] = range.copy(end = newTime)
                                onScheduleChange(schedule.copy(ranges = newRanges))
                            },
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val newRanges = schedule.ranges.toMutableList()
                                newRanges.removeAt(index)
                                onScheduleChange(schedule.copy(ranges = newRanges))
                            }
                        ) {
                            Text("×")
                        }
                    }
                }

                Button(
                    onClick = {
                        val newRanges = schedule.ranges.toMutableList().apply {
                            add(TimeRange("09:00", "17:00"))
                        }
                        onScheduleChange(schedule.copy(ranges = newRanges))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Time Range")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDropdown(
    label: String,
    selected: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TIME_SLOTS.forEach { time ->
                DropdownMenuItem(
                    text = { Text(time) },
                    onClick = {
                        onTimeSelected(time)
                        expanded = false
                    }
                )
            }
        }
    }
}

private data class DaySchedule(
    val isOpen: Boolean = true,
    val ranges: List<TimeRange> = listOf(TimeRange("09:00", "17:00"))
)

private data class TimeRange(
    val start: String,
    val end: String
)

private fun parseScheduleString(schedule: String): Map<String, DaySchedule> {
    if (schedule.isBlank()) {
        return DAYS.associateWith { DaySchedule() }
    }

    val result = mutableMapOf<String, DaySchedule>()

    try {
        schedule.split(";").forEach { daySchedule ->
            val (days, times) = daySchedule.trim().split(":")
            val timeRanges = times.trim().split(",").map { range ->
                val (start, end) = range.trim().split("-")
                TimeRange(start.trim(), end.trim())
            }

            val daysList = if (days.contains("-")) {
                val (start, end) = days.split("-")
                val startIndex = DAYS.indexOf(start.trim())
                val endIndex = DAYS.indexOf(end.trim())
                DAYS.subList(startIndex, endIndex + 1)
            } else {
                listOf(days.trim())
            }

            daysList.forEach { day ->
                result[day] = DaySchedule(isOpen = true, ranges = timeRanges)
            }
        }
    } catch (e: Exception) {
        return DAYS.associateWith { DaySchedule() }
    }

    DAYS.forEach { day ->
        if (!result.containsKey(day)) {
            result[day] = DaySchedule(isOpen = false, ranges = emptyList())
        }
    }

    return result
}

private fun formatScheduleToString(schedule: Map<String, DaySchedule>): String {
    return schedule.entries
        .filter { it.value.isOpen }
        .groupBy(
            keySelector = { it.value.ranges },
            valueTransform = { it.key }
        )
        .map { (ranges, days) ->
            val daysStr = when {
                days.size > 2 -> "${days.first()}-${days.last()}"
                days.size == 2 -> "${days[0]}, ${days[1]}"
                else -> days.first()
            }
            val rangesStr = ranges.joinToString(", ") { "${it.start}-${it.end}" }
            "$daysStr: $rangesStr"
        }
        .joinToString("; ")
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDateForButton(date: LocalDate?, defaultText: String): String {
    return date?.let {
        val month = when (it.monthValue) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> ""
        }
        "${month} ${it.dayOfMonth}, ${it.year}"
    } ?: defaultText
}