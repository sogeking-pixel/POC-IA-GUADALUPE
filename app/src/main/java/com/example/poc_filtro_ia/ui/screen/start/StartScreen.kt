package com.example.poc_filtro_ia.ui.screen.start
import androidx.compose.material.icons.Icons
import android.graphics.drawable.Icon
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poc_filtro_ia.R
import com.example.poc_filtro_ia.ui.theme.POCFILTROIATheme

val list_options = listOf(
    R.string.categoria_animal,
    R.string.categoria_buzon,
    R.string.categoria_infraestructura,
    R.string.categoria_basura,
    R.string.categoria_pista
)


@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background
){
    Surface(
        modifier = modifier.fillMaxSize().padding(vertical = 35.dp, horizontal = 15.dp ),
        color = color
    ){
        Box(
            modifier = Modifier
        ){
            ContentStart()
            ModalEvent()
        }

    }


}

@Composable
fun ContentStart(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        SpaceImagen(
            modifier = Modifier.padding(top = 10.dp)
        )
        NavList(
            options = list_options,
            modifier = Modifier.padding(vertical = 30.dp)
        )

        AnalyzeButton(
            modifier = Modifier.padding(vertical = 15.dp)
        )

        PhotoButton(
            modifier = Modifier.padding(top = 40.dp)
        )
    }

}


@Composable
fun SpaceImagen(
    modifier: Modifier = Modifier,
    imagen: Int = R.drawable.default_image
){
    val valueImage = painterResource(imagen)
    Image(
        modifier = modifier.padding(16.dp),
        painter = valueImage,
        contentDescription = null
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavList(
    options: List<Int>,
    modifier: Modifier = Modifier
){
    var optionSelect by remember { mutableIntStateOf(options[0]) }
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = modifier
    ) {
        TextField(
            value = stringResource(optionSelect),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.title_nav_list)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option)) },
                    onClick = {
                        optionSelect = option
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AnalyzeButton(
    modifier: Modifier = Modifier,
    function: () -> Unit = {},
){
    Button(
        modifier = modifier,
        onClick = function
    ) {
        Text(stringResource(R.string.text_button))
    }
}

@Composable
fun PhotoButton(
    modifier: Modifier = Modifier,
    function: () -> Unit = {},
){
    Button(
        modifier = modifier.size(56.dp),
        onClick = function,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        contentPadding = PaddingValues(10.dp),
        shape = CircleShape,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = "Tomar foto",
            tint = Color.White,
        )
    }
}

@Composable
fun ModalEvent(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.MessageAlertWarning),
    color: Color = Color.Gray,
    icon: Icon? = null,
){
    Text(message)
}

@Preview(
    showBackground = true,
    widthDp = 350,
    heightDp = 700
)
@Composable
fun GreetingPreview() {
    POCFILTROIATheme {
        StartScreen()
    }
}

