package com.example.japim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.japim.ui.components.GenerateHeader
import com.example.japim.ui.components.cards.GenerateCard
import com.example.japim.ui.components.search.GenerateSearchBar

@Composable
private fun viewStarterPage(modifier: Modifier = Modifier){
    Column(
        modifier = modifier.fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {

        GenerateSearchBar("teste",{});

        GenerateCard(
            "Lorem Ipsum",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas  interdum faucibus justo, mattis ultricies ipsum rhoncus sed. Integer  egestas dictum neque at tincidunt. Pellentesque pulvinar enim non velit  semper ....",
            "Palestrante",
            10,
            10,
            false,
            {
                println("fala");

            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun starterPage(){
    GenerateHeader(
        contentPage = { paddingResponse ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingResponse) // O padding do header entra aqui
            ) {
                viewStarterPage()
            }
        }
    );
}
