package com.ribuufing.findlostitem.presentation.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.ribuufing.findlostitem.data.model.LostItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabPagerExample(it: PaddingValues, foundItems: List<LostItem>) {
    val tabs = listOf("Lost items")
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(it)
    ) {
        TabRow(
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .pagerTabIndicatorOffset(pagerState, tabPositions)
                        .height(0.2.dp),
                    color = Color.Black
                )
            },
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            tabs = {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = {
                            Text(title, fontWeight = FontWeight.Bold)
                        }
                    )
                }
            }
        )

        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ListContent(foundItems)
            }
        }
    }
} 