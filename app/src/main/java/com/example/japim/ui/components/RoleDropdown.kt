package com.example.japim.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.graphql.RolesQuery
import com.example.japim.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    roles: List<RolesQuery.Role>,
    selectedRoleId: String?,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedRole = roles.find {
        it.id == selectedRoleId
    }

    val canSelect = enabled && !isLoading && roles.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded && canSelect,
        onExpandedChange = {
            if (canSelect) {
                expanded = it
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = when {
                isLoading -> "Carregando cargos..."
                selectedRole != null -> selectedRole.name
                roles.isEmpty() -> "Nenhum cargo disponível"
                else -> "Selecione um cargo"
            },
            onValueChange = {},
            readOnly = true,
            enabled = canSelect,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                ),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded && canSelect
                )
            },
            shape = RoundedCornerShape(50.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FOURTH,
                unfocusedContainerColor = FOURTH,
                disabledContainerColor = FOURTH,
                focusedTextColor = FIFTH,
                unfocusedTextColor = FIFTH,
                disabledTextColor = FIFTH,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTrailingIconColor = FIFTH,
                unfocusedTrailingIconColor = FIFTH,
                disabledTrailingIconColor = FIFTH
            )
        )

        ExposedDropdownMenu(
            expanded = expanded && canSelect,
            onDismissRequest = {
                expanded = false
            },
            containerColor = FOURTH
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = role.name,
                            color = FIFTH,
                            fontFamily = MPlus2,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        onRoleSelected(role.id)
                        expanded = false
                    }
                )
            }
        }
    }
}