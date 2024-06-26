package com.android.candywords.game.composables

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.constraintlayout.compose.ConstraintLayout
import com.android.candywords.R
import com.android.candywords.data.LevelData
import com.android.candywords.data.db.candies
import com.android.candywords.data.db.charactersLevelFirst
import com.android.candywords.state.CandyUiEvent
import com.android.candywords.state.CandyUiState
import com.android.candywords.state.Item

@Composable
fun GameField(
    modifier: Modifier = Modifier,
    uiState: CandyUiState,
    uiEvent: (CandyUiEvent) -> Unit
) {

    LaunchedEffect(Unit) {
        uiEvent(CandyUiEvent.UpdateColorForOneItem(itemId = R.color.candy1_background))
    }

    val state = rememberLazyGridState()
    val selectedIdSet = rememberSaveable {
        mutableStateOf(emptySet<Int>())
    }

    var isFirstItem = rememberSaveable {
        mutableStateOf(false)
    }
    var isLastItem = rememberSaveable {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        ConstraintLayout {
            val (image, grid, result) = createRefs()

            Image(
                painter = painterResource(R.drawable.field_game_1_hdpi),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    }
                    .fillMaxSize()
            )

            if (uiState.niceFineGoodState != 0) {
                Image(
                    painter = painterResource(id = uiState.niceFineGoodState),
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(result) {
                            start.linkTo(image.start)
                            end.linkTo(image.end)
                            top.linkTo(image.top)
                            bottom.linkTo(image.bottom)
                        }
                        .padding(bottom = 300.dp)
                        .height(120.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                )
            }

            LazyVerticalGrid(
                state = state,
                columns = GridCells.Fixed(uiState.currentLevel.columnsCount),
                modifier = modifier
                    .charactersDragHandler(
                        lazyGridState = state,
                        selectedIdSet = selectedIdSet,
                        uiEvent = uiEvent,
                        uiState = uiState,
                        isFirst = isFirstItem,
                        isLast = isLastItem
                    )
                    .constrainAs(grid) {
                        start.linkTo(image.start)
                        bottom.linkTo(image.bottom)
                        top.linkTo(image.top)
                        end.linkTo(image.end)
                    }
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(
                    uiState.currentLevel.characters,
                    key = { it.id }) { item ->

                    val selected by remember {
                        derivedStateOf {
                            selectedIdSet.value.contains(
                                item.id
                            )
                        }
                    }

                    LetterItem(
                        item = item,
                        isSelected = selected,
                        modifier = Modifier,
                        color = uiState.color,
                        isFirst = isFirstItem.value
                    )
                }
            }
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.charactersDragHandler(
    lazyGridState: LazyGridState,
    selectedIdSet: MutableState<Set<Int>>,
    uiEvent: (CandyUiEvent) -> Unit,
    uiState: CandyUiState,
    isFirst: MutableState<Boolean>,
    isLast: MutableState<Boolean>
) = pointerInput(Unit) {

    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
        layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Int

    var initialKey: Int? = null
    var currentKey: Int? = null

    detectDragGestures(
        onDragStart = { offset ->
            isFirst.value = true
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                if (!selectedIdSet.value.contains(key)) {
                    initialKey = key
                    currentKey = key
                    selectedIdSet.value += key
                }
            }
            uiEvent(
                CandyUiEvent.UpdateFirstItemCornerRadius(requireNotNull(initialKey))
            )
            selectedIdSet.value = emptySet()
        },

        onDrag = { change, dragAmount ->
            if (initialKey != null) {
                lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                    if (currentKey != null) {

                        selectedIdSet.value = selectedIdSet.value
                            .plus(currentKey!!)

                        if (selectedIdSet.value.contains(key)) {
                            selectedIdSet.value = selectedIdSet.value
                                .minus(currentKey!!)
                        }
                        currentKey = key
                    }
                    uiEvent(
                        CandyUiEvent.UpdateColorForOneItem(key)
                    )
                }
            }
        },

        onDragCancel = {
            initialKey = null
        },

        onDragEnd = {
            initialKey = null

            val listOfItems = uiState.currentLevel.characters.toList().filter {
                !it.isSelected && selectedIdSet.value.contains(it.id)
            }.map { it.character }

            Log.e("selected word", "${listOfItems}")

            uiEvent(
                CandyUiEvent.GetSelectedCharacters(
                    listOfChars = listOfItems
                )
            )
            uiEvent(
                CandyUiEvent.UpdateLastItemCornerRadius(requireNotNull(currentKey?.minus(1)))
            )
        }
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun GameFieldPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        GameField(
            uiState = CandyUiState(
                currentLevel = LevelData(
                    1,
                    6,
                    charactersLevelFirst,
                    candies,
                    isCompleted = false,
                    isRecentlyPlayed = true
                )
            ),
            uiEvent = {}
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun GameItem() {
    LetterItem(
        modifier = Modifier,
        item = Item(1, 'C', true, true, false, R.color.candy1_background),
        isSelected = false,
        color = R.color.candy1_background
    )
}
