package com.example.appnativa.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appnativa.components.ListProductCard
import com.example.appnativa.models.ProductCardModel
import com.example.appnativa.models.ProductCardStatus
import com.example.appnativa.service.ProductService
import com.example.compose.backgroundDark
import com.example.compose.onPrimaryContainerDark
import com.example.compose.primaryDark
import com.example.compose.secondaryDarkMediumContrast
import com.google.firebase.auth.FirebaseUser

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCart(user: FirebaseUser?) {
    val productService = ProductService()
    var total by remember { mutableStateOf(0.0) }
    var products by remember { mutableStateOf(listOf<ProductCardModel>()) }

    LaunchedEffect(Unit) {
        // Escuchar cambios en los productos del carrito en tiempo real
        productService.getShoppingCartProductsByUserRealtime { updatedProducts ->
            products = updatedProducts
            total = products.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundDark)
                .padding(0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenido de la interfaz
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
            ) {
                item {
                    ListProductCard(ProductCardStatus.SHOPPINGCART, productService, user)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .padding(top = 8.dp, bottom = 16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(secondaryDarkMediumContrast)
                            .border(0.dp, Color.White, RoundedCornerShape(10.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "$${"%.2f".format(total)}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomButton(
                                    text = "Comprar",
                                    onClick = { /* Acciones de compra */ },
                                    modifier = Modifier.height(50.dp),
                                    backgroundColor = Color.Black,
                                    contentColor = primaryDark,
                                    fontSize = 18,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


