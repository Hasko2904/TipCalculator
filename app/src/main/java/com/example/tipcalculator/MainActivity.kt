package com.example.tipcalculator

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.components.InputField
import com.example.tipcalculator.components.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                MainContent()
            }
        }
    }
}



//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){


    ///Creating Card to contain top header components and making it rounded corner shape
    Card (backgroundColor = Color(0xFFb01fa8),
    modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(corner = CornerSize(50.dp)))
        .height(150.dp)
        .padding(12.dp)
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person", style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(text = "$$total", style = MaterialTheme.typography.h4)
        }
    }
}

//@Preview
@Composable
fun MainContent() {

    val anumber = remember {
        mutableStateOf(0f)
    }
    Column {
        TopHeader(anumber.value.toDouble())
        BillForm { billAmt ->
            anumber.value = billAmt.toFloat()
        }
    }
}
    @Preview
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun BillForm(modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
                 ){

        val totalBillState = remember {
            mutableStateOf("")
        }
        val validState = remember(totalBillState.value) {
            totalBillState.value.trim().isNotEmpty()
        }
        val keyboardController = LocalSoftwareKeyboardController.current

        val final = remember {
            mutableStateOf(0f)
        }
        val tip = remember {
            mutableStateOf(0f)
        }
        val tipAmount = "%.1f".format(tip.value)

        val sliderPositionState = remember {
            mutableStateOf(0f)
        }
        val splitNumber = remember {
            mutableStateOf(1)
        }

        Surface(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            elevation = 10.dp,
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {

            Column(modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
                InputField(valueState = totalBillState,
                    lableId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (validState) return@KeyboardActions
                        keyboardController?.hide()
                    } ,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (!validState){onValChange("0")}
                if (validState){
                    final.value = (totalBillState.value.toFloat() + tip.value) / splitNumber.value
                    onValChange(final.value.toString())
                    Column{
                        Row(
                            modifier = Modifier.padding(3.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {

                            Text(
                                text = "Split",
                                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.width(120.dp))
                            Row(
                                modifier = Modifier.padding(horizontal = 3.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                                    if (splitNumber.value > 1) {
                                        splitNumber.value--
                                        final.value = (totalBillState.value.toFloat() + tip.value) / splitNumber.value
                                        onValChange(final.value.toString())
                                    }
                                })
                                Text(
                                    text = splitNumber.value.toString(),
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                                    splitNumber.value++
                                    final.value = (totalBillState.value.toFloat() + tip.value) / splitNumber.value
                                    onValChange(final.value.toString())
                                })
                            }
                        }
                    }
                    Row(modifier = Modifier.padding(vertical = 10.dp)) {
                        Text(text = "Tip", modifier = Modifier.width(230.dp))
                        Text(text = "$$tipAmount")
                    }
                    val tipPrecentage = remember {
                        mutableStateOf(0)
                    }
                    Row(modifier = Modifier.padding(vertical = 10.dp)) {
                        Text(text = "Total", modifier = Modifier.width(230.dp))

                        Text(text = "$%.1f".format(totalBillState.value.toFloat()+tipAmount.toFloat()))
                    }

                    Text(text = "%${tipPrecentage.value}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Slider(value = sliderPositionState.value , onValueChange = {
                        sliderPositionState.value = it
                        tipPrecentage.value = (it*100).toInt()
                        tip.value = (tipPrecentage.value * totalBillState.value.toFloat())/100
                        final.value = (totalBillState.value.toFloat() + tip.value) / splitNumber.value
                        onValChange(final.value.toString())
                    })
                }
            }
        }
    }



