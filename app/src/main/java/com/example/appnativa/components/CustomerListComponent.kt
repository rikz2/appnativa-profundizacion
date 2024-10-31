package com.example.appnativa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appnativa.R
import com.example.appnativa.models.CustomerModel
import com.example.appnativa.models.CustomerStatus
import com.example.appnativa.models.ProductCardModel
import com.example.appnativa.models.ProductCardStatus
import com.example.appnativa.service.CustomerService
import com.example.appnativa.service.ProductService
import com.example.compose.AppnativaTheme
import com.example.compose.errorDark
import com.example.compose.surfaceContainerDark
import com.google.firebase.auth.FirebaseUser

@Composable
fun CustomerCard(
    name: String,
    email: String,
    status: CustomerStatus,
    onDeleteClickCustomer: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .border(0.dp, Color.White, RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceContainerDark)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                CustomButtonCustomerItem(
                    text = "Eliminar",
                    onClick = onDeleteClickCustomer,
                    modifier = Modifier
                        .height(50.dp),
                    backgroundColor = Color.Transparent,
                    contentColor = errorDark,
                    fontSize = 18,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun CustomerListComponent(status: CustomerStatus, customerService: CustomerService, user: FirebaseUser?) {
    val customerState = remember { mutableStateOf<List<CustomerModel>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) } // Estado de carga

    LaunchedEffect(Unit) {
        if (user != null) { // Asegura que `user` no sea null antes de cargar
            loadCustomersForUser(user, customerService, customerState, status) {
                isLoading.value = false // Finaliza el estado de carga
            }
        } else {
            isLoading.value = false // En caso de que no haya usuario, se detiene la carga
        }
    }

    val customers = customerState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (isLoading.value) {
            Text("Cargando...", color = Color.White)
        } else if (customers.isEmpty()) {
            Text("No se encontraron clientes.", color = Color.White)
        } else {
            customers.forEach { item ->
                CustomerCard(
                    name = item.name,
                    email = item.email,
                    status = item.status,
                    onDeleteClickCustomer = {
                        customerService.deleteCustomer(item.id) { success ->
                            if (success && user != null) { // Verifica que `user` no sea null
                                loadCustomersForUser(user, customerService, customerState, status)
                            }
                        }
                    }

                )
            }
        }
    }
}

private fun loadCustomersForUser(
    user: FirebaseUser,
    customerService: CustomerService,
    customersState: MutableState<List<CustomerModel>>,
    status: CustomerStatus,
    onLoaded: () -> Unit = {}
) {
    customerService.getCustomersByUser { userCustomers ->
        customersState.value = userCustomers.filter { it.status == status && it.uid == user.uid }
        onLoaded() // Llama al callback cuando los datos están listos
    }
}


@Composable
fun CustomButtonCustomerItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White,
    fontSize: Int,
    fontWeight: FontWeight = FontWeight.Normal
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(backgroundColor)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = fontSize.sp,
            fontWeight = fontWeight
        )
    }
}