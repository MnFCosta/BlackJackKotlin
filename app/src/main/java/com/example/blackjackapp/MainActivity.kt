package com.example.blackjackapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.blackjackapp.ui.theme.BlackjackAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlackjackAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BlackJackApp()
                }
            }
        }
    }
}

@Composable
fun BlackJackApp() {
    var wins by remember{
        mutableStateOf(0)
    }
    var draws by remember{
        mutableStateOf(0)
    }
    var losses by remember{
        mutableStateOf(0)
    }
    var dealerHand by remember {
        mutableStateOf(
            listOf(
                (1..13).random(),
                (1..13).random(),
            )
        )
    }
    var playerHand by remember {
        mutableStateOf(
            listOf(
                (1..13).random(),
                (1..13).random(),
            )
        )
    }
    var dealerPoints by remember {
        mutableStateOf(0)
    }
    var playerPoints by remember {
        mutableStateOf(0)
    }
    var showDealerHand by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val hold: () -> Unit = {
        playerPoints = sumPoints(playerHand)
        showDealerHand = true
        if(playerPoints<21)
            dealerPoints = sumPoints(dealerHand)
            scope.launch {
            while(dealerPoints<17){
                delay(250)
                dealerHand += (1..13).random()
                dealerPoints = sumPoints(dealerHand)
            }
            }

    }

    val hit: () -> Unit = {
        playerHand = playerHand + (1..13).random()
        playerPoints = sumPoints(playerHand)
        if(playerPoints >= 21)
            hold()
    }

    val playAgain: () -> Unit = {
        if (playerPoints >= 21)
            losses++
        else if ( dealerPoints > 21)
            wins++
        else if ( playerPoints > dealerPoints)
            wins ++
        else if (playerPoints == dealerPoints)
            draws++
        else
            losses++
        dealerHand = listOf(
            (1..13).random(),
            (1..13).random()
        )
        playerHand = listOf(
            (1..13).random(),
            (1..13).random()
        )

        showDealerHand = false
        playerPoints = sumPoints(playerHand)
        if(playerPoints == 21)
            hold()
        dealerPoints = sumPoints(dealerHand)
    }

    MainScreen(
        wins = wins,
        draws = draws,
        losses = losses,
        dealerHand = dealerHand,
        playerHand = playerHand,
        dealerPoints = dealerPoints,
        playerPoints = playerPoints,
        showAllCards = showDealerHand,
        onHitButtonClick = hit,
        onHoldButtonClick = hold,
        onPlayAgainButtonClick = playAgain,
    )
}
@Composable
fun MainScreen(
    wins: Int,
    draws: Int,
    losses: Int,
    dealerHand: List<Int>,
    playerHand: List<Int>,
    dealerPoints: Int,
    playerPoints: Int,
    showAllCards: Boolean,
    onHitButtonClick: () -> Unit,
    onHoldButtonClick: () -> Unit,
    onPlayAgainButtonClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Scoreboard(wins = wins, draws = draws, losses = losses)
            Cards(dealerHand, showAllCards = showAllCards)
            if(showAllCards)
                GameResult(
                    playerPoints = playerPoints,
                    dealerPoints = dealerPoints,
                    onPlayAgainButtonClick = onPlayAgainButtonClick,)

            Cards(playerHand, showAllCards = true)
            PlayerActions(onHitButtonClick, onHoldButtonClick, showAllCards)
        }
    }
}

@Composable
fun Scoreboard(
    wins: Int,
    draws: Int,
    losses: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Text(text = "Wins: $wins")
        Text(text = "Draws: $draws")
        Text(text = "Losses: $losses")
    }
}

@Composable
fun Cards(
    cards: List<Int>,
    showAllCards: Boolean,
) {
    val offset = 25
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        cards.forEachIndexed{index, card ->
            val cardImage = if(!showAllCards && index==1){
                R.drawable.back
            }else {
                when (card) {
                    1 -> R.drawable.ace
                    2 -> R.drawable.two
                    3 -> R.drawable.three
                    4 -> R.drawable.four
                    5 -> R.drawable.five
                    6 -> R.drawable.six
                    7 -> R.drawable.seven
                    8 -> R.drawable.eight
                    9 -> R.drawable.nine
                    10 -> R.drawable.ten
                    11 -> R.drawable.jack
                    12 -> R.drawable.queen
                    else -> R.drawable.king
                }
            }
            Image(
                modifier = Modifier
                    .height(199.dp)
                    .width(143.dp)
                    .offset((index * offset).dp),
                painter = painterResource(id = cardImage),
                contentDescription = "card $card",
            )
        }
    }
}

@Composable
fun GameResult(
    playerPoints: Int,
    dealerPoints: Int,
    onPlayAgainButtonClick: () -> Unit
) {
    val result =
        if(playerPoints>21) "You lost, exceed 21 points!"
        else if (dealerPoints>21) "You won, dealer exceed 21 points!"
        else if (playerPoints > dealerPoints) "You won, $playerPoints x $dealerPoints!"
        else if (playerPoints == dealerPoints) "You draw, $playerPoints x $dealerPoints"
        else "You lost, $playerPoints x $dealerPoints!"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = result)
        Button(onClick = onPlayAgainButtonClick) {
            Text(text = "Play Again")
        }
    }
}

@Composable
fun PlayerActions(
    onHitButtonClick: () -> Unit,
    onHoldButtonClick: () -> Unit,
    turnEnded: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onHoldButtonClick, enabled = !turnEnded) {
            Text(text = "Hold")
        }
        Button(onClick = onHitButtonClick, enabled = !turnEnded) {
            Text(text = "Hit")
        }
    }
}

fun sumPoints(cards: List<Int>): Int{
    var aces = 0
    var points = 0
    cards.forEach{card ->
        when (card) {
            1 -> {
                points += 11
                aces += 11
            }
            11 -> points += 10
            12 -> points += 10
            13 -> points += 10
            else -> points += card
        }
        if(points>21 && aces >0){
            points -=10
            aces --
        }
    }
    return points
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BlackjackAppTheme {
        BlackJackApp()
    }
}