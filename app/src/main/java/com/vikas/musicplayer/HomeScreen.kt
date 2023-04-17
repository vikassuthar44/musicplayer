package com.vikas.musicplayer

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.Exception

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(musicList: List<AudioModel>) {

    val currentPlayingMusicPath = remember {
        mutableStateOf(AudioModel("", "", "", ""))
    }
    var currentPlayingIndex = 0
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                        .size(50.dp)
                        .background(shape = RoundedCornerShape(10.dp), color = Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            },
            title = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp)
                        .background(shape = RoundedCornerShape(size = 10.dp), color = Color.Gray)
                        .padding(all = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        tint = Color.White
                    )
                    Text(text = "Search...", style = TextStyle(color = Color.White))

                }
            }
        )
    }) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {

            if(musicList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No audio file found on your device!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp, top = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 10.dp)
                ) {
                    itemsIndexed(musicList) { index, item ->
                        SingleMusicFile(index, item) { index, audioModel ->
                            try {
                                currentPlayingIndex = index
                                currentPlayingMusicPath.value = audioModel
                            } catch (e: Exception) {

                            }
                        }
                    }
                }
            }

            if (currentPlayingMusicPath.value.aPth.isNotEmpty()) {
                val mediaPlayer = MediaPlayer()

                if (currentPlayingMusicPath.value.aPth.isNotEmpty()) {
                    mediaPlayer.setDataSource(currentPlayingMusicPath.value.aPth)
                }
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.reset()
                } else {
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
                val isPlaying = remember {
                    mutableStateOf(true)
                }
                mediaPlayer.setOnCompletionListener {
                    isPlaying.value = false
                }
                AnimatedVisibility(visible = currentPlayingMusicPath.value.aPth.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                )
                                .padding(top = 10.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentPlayingMusicPath.value.aName.ifEmpty { "" },
                                    maxLines = 1,
                                    style = TextStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                    ),
                                    fontSize = 22.sp,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Image(
                                        modifier = Modifier.clickable {
                                            if (currentPlayingIndex > 0) {
                                                currentPlayingIndex--
                                                currentPlayingMusicPath.value =
                                                    musicList[currentPlayingIndex]
                                                mediaPlayer.reset()
                                                mediaPlayer.setDataSource(currentPlayingMusicPath.value.aPth)
                                                mediaPlayer.prepare()
                                                mediaPlayer.start()
                                                isPlaying.value = true
                                            }
                                        },
                                        painter = painterResource(id = R.drawable.previous),
                                        contentDescription = ""
                                    )
                                    Image(
                                        modifier = Modifier.clickable {
                                            if (mediaPlayer.isPlaying) {
                                                Log.d(MainActivity.TAG, "HomeScreen: stop")
                                                mediaPlayer.pause()
                                            } else {
                                                Log.d(MainActivity.TAG, "HomeScreen: start")
                                                mediaPlayer.start()
                                            }
                                            isPlaying.value = !isPlaying.value
                                        },
                                        painter = if (isPlaying.value) painterResource(id = R.drawable.pause) else painterResource(
                                            id = R.drawable.play_button
                                        ),
                                        contentDescription = ""
                                    )
                                    Image(
                                        modifier = Modifier.clickable {
                                            if (currentPlayingIndex < musicList.size-1) {
                                                currentPlayingIndex++
                                                currentPlayingMusicPath.value =
                                                    musicList[currentPlayingIndex]
                                                mediaPlayer.reset()
                                                mediaPlayer.setDataSource(currentPlayingMusicPath.value.aPth)
                                                mediaPlayer.prepare()
                                                mediaPlayer.start()
                                                isPlaying.value = true
                                            }
                                        },
                                        painter = painterResource(id = R.drawable.play),
                                        contentDescription = ""
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun SingleMusicFile(
    index: Int,
    item: AudioModel,
    onClick: (index: Int, path: AudioModel) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Gray, shape = RoundedCornerShape(size = 20.dp))
            .clickable {
                onClick.invoke(index, item)
            }
            .padding(vertical = 10.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = painterResource(id = R.drawable.music_thumb)
            Box(
                modifier = Modifier
                    .background(shape = RoundedCornerShape(size = 20.dp), color = Color.Gray)
                    .height(80.dp)
                    .aspectRatio(1f),

                ) {
                Image(
                    modifier = Modifier.clip(shape = RoundedCornerShape(size = 20.dp)),
                    painter = painter,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )

            }
            Column(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = item.aName,
                    maxLines = 1,
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    ),
                    fontSize = 22.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Artist: ${item.aArtist}",
                    maxLines = 1,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                    ),
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Image(imageVector = Icons.Default.Favorite, contentDescription = "")

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    //HomeScreen()
}