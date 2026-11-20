
package com.example.japim

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import com.example.japim.graphql.GetEventByIdQuery
import androidx.compose.runtime.saveable.rememberSaveable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.japim.graphql.type.EventInputCreate
import com.example.japim.graphql.type.EventInputUpdate


private val EventYellow = Color(0xFFFFE500)
private val EventBlack = Color.Black
private val EventWhite = Color(0xFFF2F2F2)
private val EventGreen = Color(0xFF36C900)

data class EventFormData(
    val nome: String = "",
    val inicio: String = "",
    val fim: String = "",
    val conteudo: String = "",
    val descricao: String = "",
    val palestrantes: String = "",
    val participantes: String = "",
    val voluntarios: String = "",
    val imagemUri: Uri? = null,
    val imagemExistente: String? = null,
    val valorFinanceiro: String = "",
    val possuiPagamento: Boolean = false,
    val cargaHoraria: String = "",
    val palestrantesIds: List<String> = emptyList()

)


private fun formatarDataEvento(
    data: String?,
    hora: String?
): String {
    if (data.isNullOrBlank() || hora.isNullOrBlank()) {
        return ""
    }

    return try {
        val dataConvertida = LocalDate.parse(data)
        val horaConvertida = LocalTime.parse(hora)

        val dataHora = LocalDateTime.of(
            dataConvertida,
            horaConvertida
        )

        dataHora.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        )
    } catch (e: Exception) {
        ""
    }
}
private val formatoDataEvento =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

fun converterDataEvento(valor: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(valor, formatoDataEvento)
    } catch (e: Exception) {
        null
    }
}

fun GetEventByIdQuery.Event.toEventFormData(): EventFormData {
    return EventFormData(
        nome = name.orEmpty(),
        inicio = formatarDataEvento(
            date_start?.toString(),
            hour_start?.toString()
        ),

        fim = formatarDataEvento(
            date_end?.toString(),
            hour_end?.toString()
        ),
        conteudo = short_description.orEmpty(),
        descricao = description.orEmpty(),
        palestrantes = speakers
            ?.filterNotNull()
            ?.mapNotNull { it.name }
            ?.joinToString(", ")
            .orEmpty(),
        palestrantesIds = speakers
            ?.filterNotNull()
            ?.mapNotNull { it.id }
            .orEmpty(),
        participantes = max_participants?.toString().orEmpty(),
        voluntarios = max_volunteers?.toString().orEmpty(),
        imagemExistente = path_img,
        valorFinanceiro = value?.toString().orEmpty(),
        possuiPagamento = is_payment == true,
        cargaHoraria = workload?.toString().orEmpty()
    )
}

fun EventFormData.toEventInputCreate(): EventInputCreate {
    val dataInicio = requireNotNull(converterDataEvento(inicio)) {
        "Data de início inválida"
    }

    val dataFim = requireNotNull(converterDataEvento(fim)) {
        "Data de término inválida"
    }

    require(dataFim.isAfter(dataInicio)) {
        "A data de término deve ser posterior ao início"
    }

    return EventInputCreate(
        name = nome.trim(),
        short_description = conteudo.trim(),
        description = descricao.trim(),
        is_payment = possuiPagamento,
        value = if (possuiPagamento) {
            valorFinanceiro.replace(",", ".").toDouble()
        } else {
            0.0
        },
        max_participants = participantes.toInt(),
        max_volunteers = voluntarios.toInt(),
        date_start = dataInicio.toLocalDate(),
        hour_start = dataInicio.toLocalTime(),
        date_end = dataFim.toLocalDate(),
        hour_end = dataFim.toLocalTime(),
        workload = cargaHoraria.toInt(),
        speakers_id = palestrantesIds
    )
}

fun EventFormData.toEventInputUpdate(
    eventId: String
): EventInputUpdate {

    val dados = toEventInputCreate()

    return EventInputUpdate(
        id = eventId,
        name = dados.name,
        short_description = dados.short_description,
        description = dados.description,
        is_payment = dados.is_payment,
        value = dados.value,
        max_participants = dados.max_participants,
        max_volunteers = dados.max_volunteers,
        date_start = dados.date_start,
        hour_start = dados.hour_start,
        date_end = dados.date_end,
        hour_end = dados.hour_end,
        workload = dados.workload,
        speakers_id = dados.speakers_id
    )
}

@Composable
fun RegisterEvent(
    eventId: String? = null,
    dadosIniciais: EventFormData? = null,
    onVoltar: () -> Unit = {},
    onSalvar: (EventFormData) -> Unit = {}
) {
    val context = LocalContext.current
    val modoEdicao = eventId != null

    var nome by rememberSaveableEvent { mutableStateOf("") }
    var inicio by rememberSaveableEvent { mutableStateOf("") }
    var fim by rememberSaveableEvent { mutableStateOf("") }
    var conteudo by rememberSaveableEvent { mutableStateOf("") }
    var descricao by rememberSaveableEvent { mutableStateOf("") }
    var palestrantes by rememberSaveableEvent { mutableStateOf("") }
    var participantes by rememberSaveableEvent { mutableStateOf("") }
    var voluntarios by rememberSaveableEvent { mutableStateOf("") }
    var valorFinanceiro by rememberSaveableEvent { mutableStateOf("") }
    var cargaHoraria by rememberSaveableEvent { mutableStateOf("") }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    var imagemExistente by remember { mutableStateOf<String?>(null) }
    var possuiPagamento by remember { mutableStateOf(false) }

    LaunchedEffect(eventId, dadosIniciais) {
        if (modoEdicao && dadosIniciais != null) {
            nome = dadosIniciais.nome
            inicio = dadosIniciais.inicio
            fim = dadosIniciais.fim
            conteudo = dadosIniciais.conteudo
            descricao = dadosIniciais.descricao
            palestrantes = dadosIniciais.palestrantes
            participantes = dadosIniciais.participantes
            voluntarios = dadosIniciais.voluntarios
            valorFinanceiro = dadosIniciais.valorFinanceiro
            cargaHoraria = dadosIniciais.cargaHoraria
            imagemUri = dadosIniciais.imagemUri
            imagemExistente = dadosIniciais.imagemExistente
            possuiPagamento = dadosIniciais.possuiPagamento
        }
    }

    val selecionarImagem = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagemUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EventBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (modoEdicao) "Edição de Evento" else "Criação de Evento",
            color = EventYellow,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(18.dp))

        EventInput(
            value = nome,
            onValueChange = { nome = it },
            placeholder = "Nome do Evento"
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) {
                EventDateField(
                    value = inicio,
                    label = "Início do Evento",
                    onValueChange = { inicio = it }
                )
            }

            Box(Modifier.weight(1f)) {
                EventDateField(
                    value = fim,
                    label = "Fim do Evento",
                    onValueChange = { fim = it }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        EventInput(
            value = conteudo,
            onValueChange = { conteudo = it },
            placeholder = "Conteúdo do Evento"
        )

        Spacer(Modifier.height(12.dp))

        EventInput(
            value = descricao,
            onValueChange = { descricao = it },
            placeholder = "Faça uma descrição sobre seu evento",
            minLines = 5
        )

        Spacer(Modifier.height(12.dp))

        EventInput(
            value = palestrantes,
            onValueChange = { palestrantes = it },
            placeholder = "Nome do Palestrantes"
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(Modifier.weight(1f)) {
                EventInput(
                    value = participantes,
                    onValueChange = {
                        if (it.all(Char::isDigit)) participantes = it
                    },
                    placeholder = "Participantes",
                    keyboardType = KeyboardType.Number
                )
            }

            Box(Modifier.weight(1f)) {
                EventInput(
                    value = voluntarios,
                    onValueChange = {
                        if (it.all(Char::isDigit)) voluntarios = it
                    },
                    placeholder = "Voluntários",
                    keyboardType = KeyboardType.Number
                )
            }
        }

        Spacer(Modifier.height(12.dp))

//        //Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(140.dp)
//                .background(EventWhite, RoundedCornerShape(15.dp))
//                .clickable { selecionarImagem.launch("image/*") }
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = when {
//                    imagemUri != null ->
//                        "Nova imagem selecionada\nToque para alterar"
//
//                    !imagemExistente.isNullOrBlank() ->
//                        "Imagem cadastrada\nToque para substituir"
//
//                    else ->
//                        "Selecione uma imagem para customizar a visualização do evento"
//                },
//                color = EventBlack,
//                fontSize = 13.sp,
//                textAlign = TextAlign.Center
//            )
//        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = possuiPagamento,
                onCheckedChange = { marcado ->
                    possuiPagamento = marcado

                    if (!marcado) {
                        valorFinanceiro = ""
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = EventYellow,
                    uncheckedColor = EventWhite,
                    checkmarkColor = EventBlack
                )
            )

            Text(
                text = "Este evento é pago?",
                color = EventWhite,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                EventInput(
                    value = valorFinanceiro,
                    onValueChange = {
                        if (possuiPagamento) {
                            valorFinanceiro = it
                        }
                    },
                    placeholder = if (possuiPagamento) "Valor Financeiro" else "Gratuito",
                    keyboardType = KeyboardType.Decimal
                )
            }

            Box(Modifier.weight(1f)) {
                EventInput(
                    value = cargaHoraria,
                    onValueChange = { cargaHoraria = it },
                    placeholder = "Carga Horária"
                )
            }
        }

        Spacer(Modifier.height(25.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    nome = ""
                    inicio = ""
                    fim = ""
                    conteudo = ""
                    descricao = ""
                    palestrantes = ""
                    participantes = ""
                    voluntarios = ""
                    imagemUri = null
                    valorFinanceiro = ""
                    cargaHoraria = ""
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EventYellow
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Limpar", color = EventBlack)
            }

            Button(
                onClick = {
                    val dataInicio = converterDataEvento(inicio)
                    val dataFim = converterDataEvento(fim)

                    when {
                        nome.isBlank() ||
                                conteudo.isBlank() ||
                                descricao.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Preencha os campos obrigatórios",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        dataInicio == null || dataFim == null -> {
                            Toast.makeText(
                                context,
                                "Informe datas e horários válidos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        !dataFim.isAfter(dataInicio) -> {
                            Toast.makeText(
                                context,
                                "O término deve ser posterior ao início",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        participantes.toIntOrNull() == null ||
                                voluntarios.toIntOrNull() == null ||
                                cargaHoraria.toIntOrNull() == null -> {
                            Toast.makeText(
                                context,
                                "Informe participantes, voluntários e carga horária válidos",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        participantes.toInt() < 0 ||
                                voluntarios.toInt() < 0 ||
                                cargaHoraria.toInt() <= 0 -> {
                            Toast.makeText(
                                context,
                                "As quantidades devem ser positivas e a carga horária maior que zero",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        possuiPagamento &&
                                (valorFinanceiro.replace(",", ".").toFloatOrNull() == null ||
                                        valorFinanceiro.replace(",", ".").toFloatOrNull()!! <= 0f) -> {
                            Toast.makeText(
                                context,
                                "Informe um valor financeiro válido",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            onSalvar(
                                EventFormData(
                                    nome = nome,
                                    inicio = inicio,
                                    fim = fim,
                                    conteudo = conteudo,
                                    descricao = descricao,
                                    palestrantes = palestrantes,
                                    participantes = participantes,
                                    voluntarios = voluntarios,
                                    imagemUri = imagemUri,
                                    imagemExistente = imagemExistente,
                                    valorFinanceiro = valorFinanceiro,
                                    possuiPagamento = possuiPagamento,
                                    cargaHoraria = cargaHoraria,
                                    palestrantesIds = dadosIniciais?.palestrantesIds.orEmpty()
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EventGreen
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = if (modoEdicao) "Salvar" else "Solicitar/Criar",
                    color = EventWhite,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onVoltar) {
            Text("Voltar", color = EventYellow)
        }
    }
}

@Composable
private fun rememberSaveableEvent(
    init: () -> MutableState<String>
): MutableState<String> = rememberSaveable { init() }

@Composable
private fun EventInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                placeholder,
                color = Color.DarkGray,
                fontSize = 12.sp
            )
        },
        minLines = minLines,
        singleLine = minLines == 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = EventWhite,
            unfocusedContainerColor = EventWhite,
            focusedTextColor = EventBlack,
            unfocusedTextColor = EventBlack,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun EventDateField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .background(EventWhite, RoundedCornerShape(25.dp))
            .clickable {
                val calendar = Calendar.getInstance()

                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                onValueChange(
                                    "%02d/%02d/%04d %02d:%02d".format(
                                        day,
                                        month + 1,
                                        year,
                                        hour,
                                        minute
                                    )
                                )
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.ifBlank { label },
            color = EventBlack,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}
