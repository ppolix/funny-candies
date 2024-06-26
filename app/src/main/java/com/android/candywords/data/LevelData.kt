package com.android.candywords.data

import com.android.candywords.data.db.candiesLevelFirst
import com.android.candywords.data.db.charactersLevelFirst
import com.android.candywords.state.Item

data class LevelData(
    val number: Int,
    val columnsCount: Int,
    val characters: List<Item>,
    val listOfCandies: List<Candy>,
    val isCompleted: Boolean,
    val isRecentlyPlayed: Boolean
)

data class Candy(
    val id: Int,
    val name: List<Char>,
    val isOpened: Boolean
)

val firstLevel = LevelData(
    number = 1,
    columnsCount = 6,
    characters = charactersLevelFirst,
    listOfCandies = candiesLevelFirst,
    isCompleted = false,
    isRecentlyPlayed = true
)

val secondLevel = LevelData(
    number = 2,
    columnsCount = 6,
    characters = charactersLevelFirst,
    listOfCandies = candiesLevelFirst,
    isCompleted = false,
    isRecentlyPlayed = true
)

val thirdLevel = LevelData(
    number = 3,
    columnsCount = 7,
    characters = charactersLevelFirst,
    listOfCandies = candiesLevelFirst,
    isCompleted = false,
    isRecentlyPlayed = true
)
val fourthLevel = LevelData(
    number = 4,
    columnsCount = 6,
    characters = charactersLevelFirst,
    listOfCandies = candiesLevelFirst,
    isCompleted = false,
    isRecentlyPlayed = true
)

val fifthLevel = LevelData(
    number = 5,
    columnsCount = 7,
    characters = charactersLevelFirst,
    listOfCandies = candiesLevelFirst,
    isCompleted = false,
    isRecentlyPlayed = true
)


val sixLevel = LevelData(
    number = 6,
    columnsCount = 7,
    characters = charactersLevelFirst,
    listOfCandies = candiesLevelFirst,
    isCompleted = false,
    isRecentlyPlayed = true
)

