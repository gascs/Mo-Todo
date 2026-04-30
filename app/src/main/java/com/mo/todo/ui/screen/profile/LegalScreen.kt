package com.mo.todo.ui.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import compose.icons.Octicons
import compose.icons.octicons.ArrowLeft24
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mo.todo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    type: String,
    onNavigateBack: () -> Unit
) {
    val title = when (type) {
        "privacy" -> stringResource(R.string.legal_privacy_title)
        "terms" -> stringResource(R.string.legal_terms_title)
        "disclaimer" -> stringResource(R.string.legal_disclaimer_title)
        "open_source" -> stringResource(R.string.legal_open_source_title)
        else -> ""
    }

    val content = when (type) {
        "privacy" -> stringResource(R.string.about_privacy_desc)
        "terms" -> stringResource(R.string.legal_terms_content)
        "disclaimer" -> stringResource(R.string.legal_disclaimer_content)
        "open_source" -> stringResource(R.string.about_open_source_full_desc)
        else -> ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Octicons.ArrowLeft24, contentDescription = null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
