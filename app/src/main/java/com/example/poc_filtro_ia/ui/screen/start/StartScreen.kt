package com.example.poc_filtro_ia.ui.screen.start

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poc_filtro_ia.R
import com.example.poc_filtro_ia.ui.UiState
import com.example.poc_filtro_ia.ui.theme.POCFILTROIATheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val list_options = listOf(
    R.string.categoria_animal,
    R.string.categoria_buzon,
    R.string.categoria_infraestructura,
    R.string.categoria_basura,
    R.string.categoria_pista
)


@SuppressLint("DefaultLocale")
@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    viewModel: StartViewModel = StartViewModel()
){

    val optionText = stringResource(id = list_options[0])
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf<String>(optionText) }
    val imageState by viewModel.imageState.collectAsState()
    Surface(
        modifier = modifier.fillMaxSize().padding(vertical = 35.dp, horizontal = 15.dp ),
        color = color
    ){
        Box(
            modifier = Modifier
        )
        {
            ContentStart(
                viewModel = viewModel,
                onSelectedCategory = {
                    selectedCategory = it
                    viewModel.changeCategory()
                }
            )
            if(uiState !is UiState.Initial)
                when (val currentState = uiState) {
                    is UiState.LoadingTensorFlow -> {
                        ModalEvent(
                            message = "Analizando imagen…",
                            color = Color(0xFF2196F3),
                        )
                    }
                    is UiState.SuccessTensorFlow -> {
                        if(imageState.isAnalyzed){
                            val isCorrect = currentState.outputText == selectedCategory
                            val confidencePct = String.format("%.1f", currentState.outputScale * 100)
                            ModalEvent(
                                message = if (isCorrect) {
                                    "¡Correcto! ${currentState.outputText} ($confidencePct)"
                                } else {
                                    "Incorrecto. Predijo ${currentState.outputText} ($confidencePct)"
                                },
                                color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                            )
                        }
                    }
                    is UiState.Error -> {
                        ModalEvent(
                            message = stringResource(id = R.string.MessageAlertError),
                            color = Color(0xFFF44336),
                        )
                    }
                    else -> {}
                }

        }

    }


}

@Composable
fun ContentStart(
    viewModel: StartViewModel,
    modifier: Modifier = Modifier,
    onSelectedCategory: (String) -> Unit = {},
){
    val context = LocalContext.current
    val imageState by viewModel.imageState.collectAsState()
    var imageUri: Uri? by rememberSaveable { mutableStateOf(null) }
    val scope: CoroutineScope = rememberCoroutineScope()
    val focusManager: FocusManager = LocalFocusManager.current
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            imageUri?.let { uri ->
                scope.launch {
                    viewModel.handlePhotoResult(success, uri, context)
                }
            }
        }
    )

    val onTakePhotoLambda: () -> Unit = {
        focusManager.clearFocus()
        scope.launch {
            val fileUri = viewModel.preparePhotoUri(context)
            imageUri = fileUri
            photoLauncher.launch(fileUri)
            Log.d("imagen Changed", "ui link image $imageUri")
        }
    }


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        SpaceImagen(
            image = imageState.photoUris,
            modifier = Modifier.padding(top = 10.dp)
        )
        NavList(
            options = list_options,
            modifier = Modifier.padding(vertical = 30.dp),
            onSelectedCategory = onSelectedCategory
        )

        AnalyzeButton(
            onClick = onclick@{
                val bitmap = viewModel.uriToBitmap(context,imageState.photoUris)
                if (bitmap == null) return@onclick
                viewModel.classifierImage(bitmap, context)
                },
            modifier = Modifier.padding(vertical = 15.dp)
        )

        PhotoButton(
            onClick = onTakePhotoLambda,
            modifier = Modifier.padding(top = 40.dp)
        )
    }

}


@Composable
fun SpaceImagen(
    modifier: Modifier = Modifier,
    image: Uri? = null,
){
    val defaultImage = painterResource(R.drawable.default_image)
    val painter: Painter
    if (image == null) {
        Log.d("imagen Changed", "default")
        painter  =  defaultImage
    } else {
        painter  = rememberAsyncImagePainter(image)
        Log.d("imagen Changed", "ui link image")
    }
    Image(
        modifier = modifier.padding(16.dp),
        painter = painter ,
        contentDescription = null
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavList(
    options: List<Int>,
    modifier: Modifier = Modifier,
    onSelectedCategory: (String) -> Unit = {}
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
                val optionText = stringResource(id = option)
                DropdownMenuItem(
                    text = { Text(optionText) },
                    onClick = {
                        optionSelect = option
                        isExpanded = false
                        onSelectedCategory(optionText)
                    }
                )
            }
        }
    }
}

@Composable
fun AnalyzeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
){
    Button(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(stringResource(R.string.text_button))
    }
}

@Composable
fun PhotoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
){
    Button(
        modifier = modifier.size(56.dp),
        onClick = onClick,
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
){
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        color = color,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
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

