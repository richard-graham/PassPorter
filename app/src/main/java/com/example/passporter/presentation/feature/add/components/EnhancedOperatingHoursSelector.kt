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
import androidx.compose.runtime.LaunchedEffect
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

    val schedule = when (selectedSeason) {
        "summer" -> operatingHours.summerHours?.schedule
        "winter" -> operatingHours.winterHours?.schedule
        else -> operatingHours.regular
    }?.let { parseScheduleString(it) } ?: DAYS.associateWith { DaySchedule() }

    val isScheduleEmpty = schedule.values.all { !it.isOpen || it.ranges.isEmpty() }
    val allDaysHaveSameSchedule = schedule.values.distinct().size == 1
    val defaultTimeRange = if (schedule.values.isNotEmpty()) {
        val firstDay = schedule.values.first()
        if (firstDay.ranges.isNotEmpty()) firstDay.ranges.first() else TimeRange("09:00", "17:00")
    } else {
        TimeRange("09:00", "17:00")
    }

    var showDetailedView by remember {
        mutableStateOf(!(allDaysHaveSameSchedule || isScheduleEmpty))
    }

    // Track if the schedule is open for the entire week (used for the main toggle)
    var allDaysOpen by remember(schedule) {
        mutableStateOf(schedule.values.all { it.isOpen })
    }

    LaunchedEffect(schedule) {
        val newIsEmpty = schedule.values.all { !it.isOpen || it.ranges.isEmpty() }
        val newAllSame = schedule.values.distinct().size == 1
        showDetailedView = !(newAllSame || newIsEmpty)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
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
                modifier = Modifier.menuAnchor().fillMaxWidth()
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

        // Simplified view toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show detailed schedule",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = showDetailedView,
                onCheckedChange = { showDetailedView = it }
            )
        }

        if (!showDetailedView) {
            // Simplified view - single card for all days
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Toggle for the entire week
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Open all week",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = allDaysOpen,
                            onCheckedChange = { isOpen ->
                                allDaysOpen = isOpen

                                // Update schedule for all days
                                val updatedSchedule = DAYS.associateWith { day ->
                                    val currentSchedule = schedule[day] ?: DaySchedule()
                                    val updatedRanges = if (isOpen && currentSchedule.ranges.isEmpty()) {
                                        listOf(defaultTimeRange)
                                    } else {
                                        currentSchedule.ranges
                                    }
                                    currentSchedule.copy(isOpen = isOpen, ranges = updatedRanges)
                                }

                                val formattedSchedule = formatScheduleToString(updatedSchedule)
                                updateOperatingHours(
                                    operatingHours,
                                    selectedSeason,
                                    formattedSchedule,
                                    onOperatingHoursChange
                                )
                            }
                        )
                    }

                    if (allDaysOpen) {
                        // Time selector for the whole week
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimePickerDropdown(
                                label = "Open",
                                selected = defaultTimeRange.start,
                                onTimeSelected = { newTime ->
                                    // Update start time for all days
                                    val updatedSchedule = schedule.mapValues { (_, daySchedule) ->
                                        if (daySchedule.isOpen && daySchedule.ranges.isNotEmpty()) {
                                            val updatedRanges = daySchedule.ranges.map { it.copy(start = newTime) }
                                            daySchedule.copy(ranges = updatedRanges)
                                        } else {
                                            daySchedule
                                        }
                                    }

                                    val formattedSchedule = formatScheduleToString(updatedSchedule)
                                    updateOperatingHours(
                                        operatingHours,
                                        selectedSeason,
                                        formattedSchedule,
                                        onOperatingHoursChange
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )

                            Text("to")

                            TimePickerDropdown(
                                label = "Close",
                                selected = defaultTimeRange.end,
                                onTimeSelected = { newTime ->
                                    // Update end time for all days
                                    val updatedSchedule = schedule.mapValues { (_, daySchedule) ->
                                        if (daySchedule.isOpen && daySchedule.ranges.isNotEmpty()) {
                                            val updatedRanges = daySchedule.ranges.map { it.copy(end = newTime) }
                                            daySchedule.copy(ranges = updatedRanges)
                                        } else {
                                            daySchedule
                                        }
                                    }

                                    val formattedSchedule = formatScheduleToString(updatedSchedule)
                                    updateOperatingHours(
                                        operatingHours,
                                        selectedSeason,
                                        formattedSchedule,
                                        onOperatingHoursChange
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Show summary of the schedule
                        val formattedScheduleSummary = if (allDaysHaveSameSchedule) {
                            val firstDay = schedule.values.first()
                            if (firstDay.isOpen && firstDay.ranges.isNotEmpty()) {
                                val range = firstDay.ranges.first()
                                "All days: ${range.start} - ${range.end}"
                            } else {
                                "All days closed"
                            }
                        } else {
                            "Custom schedule (varies by day)"
                        }

                        Text(
                            text = formattedScheduleSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            // Detailed view - individual cards for each day
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
                            updateOperatingHours(
                                operatingHours,
                                selectedSeason,
                                formattedSchedule,
                                onOperatingHoursChange
                            )
                        }
                    )
                }
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
                        val newRanges = if (isOpen && schedule.ranges.isEmpty()) {
                            // Add default time range when toggling from closed to open
                            listOf(TimeRange("09:00", "17:00"))
                        } else {
                            schedule.ranges
                        }
                        onScheduleChange(schedule.copy(isOpen = isOpen, ranges = newRanges))
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
        // Split the schedule string by semicolons to get each day or day range
        schedule.split(";").forEach { dayScheduleStr ->
            val daySchedule = dayScheduleStr.trim()
            if (daySchedule.isNotEmpty()) {
                // Find the position of the first colon - this separates days from time ranges
                val colonPos = daySchedule.indexOf(':')
                if (colonPos > 0) {
                    val daysStr = daySchedule.substring(0, colonPos).trim()
                    val timesStr = daySchedule.substring(colonPos + 1).trim()

                    // Check if this is a closed day marker
                    val isClosed = timesStr.trim().equals("CLOSED", ignoreCase = true)

                    // Parse the time ranges if not closed
                    val timeRanges = if (!isClosed) {
                        timesStr.split(",").map { range ->
                            val rangeParts = range.trim().split("-")
                            if (rangeParts.size == 2) {
                                TimeRange(rangeParts[0].trim(), rangeParts[1].trim())
                            } else {
                                // Default range if format is incorrect
                                TimeRange("09:00", "17:00")
                            }
                        }
                    } else {
                        emptyList() // No time ranges for closed days
                    }

                    // Parse the days or day range
                    val daysList = if (daysStr.contains("-")) {
                        // Handle day range like "Monday-Friday"
                        val rangeParts = daysStr.split("-")
                        if (rangeParts.size == 2) {
                            val startDay = rangeParts[0].trim()
                            val endDay = rangeParts[1].trim()
                            val startIndex = DAYS.indexOf(startDay)
                            val endIndex = DAYS.indexOf(endDay)
                            if (startIndex >= 0 && endIndex >= 0) {
                                if (startIndex <= endIndex) {
                                    // Normal range like Monday-Friday
                                    DAYS.subList(startIndex, endIndex + 1)
                                } else {
                                    // Wrapping range like Sunday-Tuesday (wraps around week)
                                    DAYS.subList(startIndex, DAYS.size) + DAYS.subList(0, endIndex + 1)
                                }
                            } else {
                                listOf(startDay)
                            }
                        } else {
                            listOf(daysStr)
                        }
                    } else if (daysStr.contains(",")) {
                        // Handle comma-separated days like "Monday, Tuesday"
                        daysStr.split(",").map { it.trim() }
                    } else {
                        // Single day
                        listOf(daysStr)
                    }

                    // Set the schedule for each day
                    daysList.forEach { day ->
                        if (DAYS.contains(day)) {
                            result[day] = DaySchedule(
                                isOpen = !isClosed,
                                ranges = if (isClosed) emptyList() else timeRanges
                            )
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        // In case of any parsing error, return default schedule
        return DAYS.associateWith { DaySchedule() }
    }

    // Set default for any day not specified in the schedule
    DAYS.forEach { day ->
        if (!result.containsKey(day)) {
            result[day] = DaySchedule(isOpen = true, ranges = listOf(TimeRange("09:00", "17:00")))
        }
    }

    return result
}

// Also update formatScheduleToString to better handle all-day ranges
private fun formatScheduleToString(schedule: Map<String, DaySchedule>): String {
    // Make sure we debug our input
    // println("formatScheduleToString input: ${schedule.entries.joinToString { "${it.key}=${it.value.isOpen}" }}")

    // First, handle the open days with their time ranges
    val openDaysString = schedule.entries
        .filter { it.value.isOpen && it.value.ranges.isNotEmpty() }
        .groupBy(
            keySelector = { entry -> entry.value.ranges },
            valueTransform = { entry -> entry.key }
        )
        .map { (ranges, days) ->
            // Find consecutive day sequences properly
            val formattedDays = formatDaysWithSequences(days.sorted())

            // Format the time ranges
            val rangesStr = ranges.joinToString(", ") { "${it.start}-${it.end}" }

            // Combine days and ranges
            "$formattedDays: $rangesStr"
        }
        .filter { it.isNotEmpty() }
        .joinToString("; ")

    // Now, handle closed days by adding a special marker
    val closedDays = schedule.entries
        .filter { !it.value.isOpen }
        .map { it.key }
        .sorted()

    // If we have closed days, add them to the string with a special marker
    return if (closedDays.isEmpty()) {
        openDaysString
    } else {
        // Format closed days using the same sequence detection logic
        val closedDaysString = formatDaysWithSequences(closedDays) + ": CLOSED"

        // Combine open and closed days strings
        if (openDaysString.isNotEmpty()) {
            "$openDaysString; $closedDaysString"
        } else {
            closedDaysString
        }
    }
}

/**
 * Formats a list of days, properly handling consecutive sequences.
 * Returns a formatted string like "Monday-Friday" or "Monday, Wednesday, Friday"
 */
private fun formatDaysWithSequences(days: List<String>): String {
    if (days.isEmpty()) return ""
    if (days.size == 1) return days.first()

    // Make sure days are sorted by their order in the week
    val sortedDays = days.sortedBy { DAYS.indexOf(it) }

    // Optimization for when all days are present
    if (sortedDays.size == 7 &&
        sortedDays.containsAll(DAYS)) {
        return "Monday-Sunday"
    }

    // Find consecutive sequences
    val sequences = mutableListOf<List<String>>()
    var currentSequence = mutableListOf(sortedDays.first())

    for (i in 1 until sortedDays.size) {
        val prevDayIndex = DAYS.indexOf(currentSequence.last())
        val currDayIndex = DAYS.indexOf(sortedDays[i])

        if (currDayIndex == prevDayIndex + 1) {
            // Consecutive day, add to current sequence
            currentSequence.add(sortedDays[i])
        } else {
            // Gap found, start a new sequence
            sequences.add(currentSequence.toList())
            currentSequence = mutableListOf(sortedDays[i])
        }
    }

    // Add the last sequence
    if (currentSequence.isNotEmpty()) {
        sequences.add(currentSequence.toList())
    }

    // Format each sequence
    return sequences.joinToString("; ") { seq ->
        when {
            seq.size > 2 -> "${seq.first()}-${seq.last()}"
            seq.size == 2 -> "${seq[0]}, ${seq[1]}"
            else -> seq[0]
        }
    }
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
        "$month ${it.dayOfMonth}, ${it.year}"
    } ?: defaultText
}

// Helper function to update operating hours based on season
@RequiresApi(Build.VERSION_CODES.O)
private fun updateOperatingHours(
    operatingHours: OperatingHours,
    selectedSeason: String,
    formattedSchedule: String,
    onOperatingHoursChange: (OperatingHours) -> Unit
) {
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