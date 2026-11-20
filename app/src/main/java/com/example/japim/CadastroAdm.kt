package com.example.japim


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.ui.theme.*

@Composable
fun CadastroAdm(
    modifier: Modifier = Modifier,
    onVoltar: () -> Unit = {}
) {
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var ra by remember { mutableStateOf("") }
    var alunoFucapi by remember { mutableStateOf(false) }

    var mensagemErro by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PRIMARY)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo_japim_preto),
            contentDescription = "Logo do Japim",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Cadastro Administrativo",
            color = SECONDARY,
            fontFamily = MPlus2,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampoCadastro(
            valor = nome,
            aoAlterar = { nome = it },
            texto = "Insira o Nome"
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoCadastro(
            valor = sobrenome,
            aoAlterar = { sobrenome = it },
            texto = "Insira o Sobrenome"
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoCadastro(
            valor = cpf,
            aoAlterar = {
                if (it.length <= 11 && it.all(Char::isDigit)) {
                    cpf = it
                }
            },
            texto = "Insira o CPF",
            tipoTeclado = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoCadastro(
            valor = email,
            aoAlterar = { email = it },
            texto = "Insira o E-mail",
            tipoTeclado = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoCadastro(
            valor = senha,
            aoAlterar = { senha = it },
            texto = "Insira a Senha",
            senha = true,
            tipoTeclado = KeyboardType.Password
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = alunoFucapi,
                onCheckedChange = {
                    alunoFucapi = it
                    if (!it) ra = ""
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = SECONDARY,
                    uncheckedColor = SECONDARY,
                    checkmarkColor = PRIMARY
                )
            )

            Text(
                text = "Aluno da FUCAPI",
                color = SECONDARY,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
        }

        if (alunoFucapi) {
            CampoCadastro(
                valor = ra,
                aoAlterar = { ra = it },
                texto = "Insira o RA",
                tipoTeclado = KeyboardType.Number
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        mensagemErro?.let {
            Text(
                text = it,
                color = Color.Red,
                fontFamily = MPlus2,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = {
                mensagemErro = when {
                    nome.isBlank() ||
                            sobrenome.isBlank() ||
                            cpf.isBlank() ||
                            email.isBlank() ||
                            senha.isBlank() ->
                        "Preencha todos os campos obrigatórios."

                    cpf.length != 11 ->
                        "O CPF deve conter 11 dígitos."

                    !android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email.trim()).matches() ->
                        "Informe um e-mail válido."

                    alunoFucapi && ra.isBlank() ->
                        "Informe o RA."

                    else ->
                        "Interface pronta. O cadastro será conectado à API posteriormente."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SUCCESS
            )
        ) {
            Text(
                text = "Cadastrar Usuário",
                color = FOURTH,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                nome = ""
                sobrenome = ""
                cpf = ""
                email = ""
                senha = ""
                ra = ""
                alunoFucapi = false
                mensagemErro = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WARNING
            )
        ) {
            Text(
                text = "Limpar",
                color = PRIMARY,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = onVoltar,
            modifier = Modifier
                .width(160.dp)
                .height(40.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = THIRD
            )
        ) {
            Text(
                text = "Voltar",
                color = PRIMARY,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
