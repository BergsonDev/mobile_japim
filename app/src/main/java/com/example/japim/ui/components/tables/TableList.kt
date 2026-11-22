package com.example.japim.ui.components.tables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.example.japim.ui.theme.SUCCESS
import kotlinx.coroutines.launch


@Composable
fun GenerateListPresence(
    event_id: String,
    members : List<IMembersEvent>,
    onCheckedPresence : suspend (event_id : String, member_id : String)-> Boolean,
    onCheckedRemovePresence : suspend (event_id : String, member_id : String)-> Boolean,
){
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier=Modifier
            .fillMaxSize()
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SECONDARY),
    ) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(members) { member ->
                var isChecked by remember(member.id, member.isChecked) { mutableStateOf(member.isChecked) }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .background(SECONDARY)
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        member.name,
                        color = PRIMARY
                    )

                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { clicked ->
                            val previousState = isChecked
                            isChecked = clicked
                            coroutineScope.launch {
                                val success = if (clicked) {
                                    onCheckedPresence(event_id, member.id)
                                } else {
                                    onCheckedRemovePresence(event_id, member.id)
                                }
                                if (!success) {
                                    isChecked = previousState
                                }
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            SUCCESS,
                            PRIMARY
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    thickness = 1.dp,
                    color = PRIMARY
                )
            }
        }
    }
}
