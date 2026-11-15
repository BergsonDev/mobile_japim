
package com.example.japim

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.ui.theme.MPlus2

@Composable
fun Cadastro(modifier: Modifier = Modifier) {

    // Variáveis dos campos
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var ra by remember { mutableStateOf("") }
    var alunoFucapi by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo do Japim
        Image(
            painter = painterResource(
                id = R.drawable.logo_japim_preto
            ),
            contentDescription = "Logo do Japim",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Título
        Text(
            text = "Criação de Conta",
            color = Color(0xFFFFD600),
            fontFamily = MPlus2,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Campo Nome
        CampoCadastro(
            valor = nome,
            aoAlterar = { nome = it },
            texto = "Insira seu Nome"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo Sobrenome
        CampoCadastro(
            valor = sobrenome,
            aoAlterar = { sobrenome = it },
            texto = "Insira seu Sobrenome"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo CPF
        CampoCadastro(
            valor = cpf,
            aoAlterar = {
                if (it.length <= 11 && it.all { c -> c.isDigit() }) {
                    cpf = it
                }
            },
            texto = "Insira seu CPF",
            tipoTeclado = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo E-mail
        CampoCadastro(
            valor = email,
            aoAlterar = { email = it },
            texto = "Insira seu E-mail",
            tipoTeclado = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo Senha
        CampoCadastro(
            valor = senha,
            aoAlterar = { senha = it },
            texto = "Insira sua Senha",
            senha = true,
            tipoTeclado = KeyboardType.Password
        )

        // Checkbox aluno FUCAPI
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = alunoFucapi,
                onCheckedChange = {
                    alunoFucapi = it

                    if (!it) {
                        ra = ""
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFFFD600),
                    uncheckedColor = Color(0xFFFFD600),
                    checkmarkColor = Color.Black
                )
            )

            Text(
                text = "Sou Aluno da FUCAPI",
                color = Color(0xFFFFD600),
                fontFamily = MPlus2,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
        }

        // Campo RA
        CampoCadastro(
            valor = ra,
            aoAlterar = { ra = it },
            texto = "Insira seu RA",
            habilitado = alunoFucapi,
            tipoTeclado = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Botão Entrar
        Button(
            onClick = {
                // Futuramente: enviar cadastro para a API
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF35C900)
            )
        ) {
            Text(
                text = "Entrar",
                color = Color.White,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botão Limpar
        Button(
            onClick = {
                nome = ""
                sobrenome = ""
                cpf = ""
                email = ""
                senha = ""
                ra = ""
                alunoFucapi = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow
            )
        ) {
            Text(
                text = "Limpar",
                color = Color.Black,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(70.dp))

        // Botão Voltar ao Login
        Button(
            onClick = {
                // Futuramente: navegar para o Login
            },
            modifier = Modifier
                .width(160.dp)
                .height(40.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF60AFDA)
            )
        ) {
            Text(
                text = "Voltar ao Login",
                color = Color.White,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


// Componente reutilizável para os campos
@Composable
fun CampoCadastro(
    valor: String,
    aoAlterar: (String) -> Unit,
    texto: String,
    senha: Boolean = false,
    habilitado: Boolean = true,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {

    TextField(
        value = valor,
        onValueChange = aoAlterar,

        modifier = Modifier.fillMaxWidth(),

        enabled = habilitado,

        singleLine = true,

        placeholder = {
            Text(
                text = texto,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.Black
            )
        },

        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontFamily = MPlus2,
            fontSize = 14.sp
        ),

        shape = RoundedCornerShape(50.dp),

        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado
        ),

        visualTransformation = if (senha) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },

        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF1F3F4),
            unfocusedContainerColor = Color(0xFFF1F3F4),
            disabledContainerColor = Color(0xFFD0D0D0),

            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,

            cursorColor = Color.Black
        )
    )
}








@Preview(showBackground = true)
@Composable
private fun CadastroPreview() {
    Cadastro()
}
