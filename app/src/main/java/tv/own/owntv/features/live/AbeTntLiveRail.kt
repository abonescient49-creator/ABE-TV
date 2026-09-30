package tv.own.owntv.features.live

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.core.theme.GlassSurface
import tv.own.owntv.ui.components.FocusableSurface
import tv.own.owntv.ui.theme.OwnTVTheme

/**
 * ABE TV French DTT launcher rail.
 *
 * ARTE stays inside the native player. Broadcasters that require their own authenticated/DRM
 * environment are exposed as first-class channel choices and handed to their official Android TV
 * app (or the Play Store search if the app is not installed).
 */
@Composable
fun AbeTntLiveRail(
    onOpenArte: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = OwnTVTheme.colors

    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column {
                Text(
                    text = stringResource(R.string.abe_live_tnt_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = stringResource(R.string.abe_live_tnt_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ABE_TNT, key = { it.number }) { channel ->
                FocusableSurface(
                    onClick = {
                        if (channel.provider == Provider.NATIVE_ARTE) onOpenArte()
                        else openOfficialProvider(context, channel.provider)
                    },
                    modifier = Modifier.width(150.dp),
                    shape = RoundedCornerShape(18.dp),
                    surface = GlassSurface.CARDS,
                    focusedContainerColor = colors.primaryContainer,
                    unfocusedContainerColor = colors.surfaceContainerHigh,
                ) { focused ->
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 13.dp)) {
                        Text(
                            text = channel.number.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (focused) colors.onPrimaryContainer.copy(alpha = 0.72f) else colors.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (focused) colors.onPrimaryContainer else colors.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (channel.provider == Provider.NATIVE_ARTE) {
                                stringResource(R.string.abe_live_native)
                            } else {
                                stringResource(R.string.abe_live_official_app)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (focused) colors.onPrimaryContainer.copy(alpha = 0.76f) else colors.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

private fun openOfficialProvider(context: Context, provider: Provider) {
    val candidates = when (provider) {
        Provider.TF1 -> listOf("fr.tf1.mytf1")
        Provider.FRANCE_TV -> listOf("fr.francetv.pluzz")
        Provider.M6 -> listOf("fr.m6.m6replay")
        Provider.RMC -> listOf(
            "com.nextinteractive.rmcbfmplay.tv.sfr",
            "com.nextinteractive.rmcbfmplay.tv",
            "com.nextinteractive.rmcbfmplay",
        )
        Provider.CANAL -> listOf("com.canal.android.canal")
        Provider.NRJ -> emptyList()
        Provider.T18 -> emptyList()
        Provider.NATIVE_ARTE -> emptyList()
    }

    candidates.firstNotNullOfOrNull { pkg ->
        context.packageManager.getLeanbackLaunchIntentForPackage(pkg)
            ?: context.packageManager.getLaunchIntentForPackage(pkg)
    }?.let { intent ->
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return
    }

    val query = when (provider) {
        Provider.TF1 -> "TF1+"
        Provider.FRANCE_TV -> "france.tv"
        Provider.M6 -> "M6+"
        Provider.RMC -> "RMC BFM Play"
        Provider.CANAL -> "CANAL+"
        Provider.NRJ -> "NRJ Play"
        Provider.T18 -> "T18"
        Provider.NATIVE_ARTE -> "ARTE"
    }
    val market = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://search?q=" + Uri.encode(query) + "&c=apps"),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    runCatching { context.startActivity(market) }.getOrElse {
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/search?q=" + Uri.encode(query) + "&c=apps"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(web) }
    }
}

private data class AbeTntChannel(val number: Int, val name: String, val provider: Provider)

private enum class Provider { TF1, FRANCE_TV, M6, NATIVE_ARTE, RMC, CANAL, NRJ, T18 }

private val ABE_TNT = listOf(
    AbeTntChannel(1, "TF1", Provider.TF1),
    AbeTntChannel(2, "France 2", Provider.FRANCE_TV),
    AbeTntChannel(3, "France 3", Provider.FRANCE_TV),
    AbeTntChannel(4, "France 4", Provider.FRANCE_TV),
    AbeTntChannel(5, "France 5", Provider.FRANCE_TV),
    AbeTntChannel(6, "M6", Provider.M6),
    AbeTntChannel(7, "ARTE", Provider.NATIVE_ARTE),
    AbeTntChannel(8, "LCP · Public Sénat", Provider.FRANCE_TV),
    AbeTntChannel(9, "W9", Provider.M6),
    AbeTntChannel(10, "TMC", Provider.TF1),
    AbeTntChannel(11, "TFX", Provider.TF1),
    AbeTntChannel(12, "Gulli", Provider.M6),
    AbeTntChannel(13, "BFM TV", Provider.RMC),
    AbeTntChannel(14, "CNEWS", Provider.CANAL),
    AbeTntChannel(15, "LCI", Provider.TF1),
    AbeTntChannel(16, "franceinfo:", Provider.FRANCE_TV),
    AbeTntChannel(17, "CStar", Provider.CANAL),
    AbeTntChannel(18, "T18", Provider.T18),
    AbeTntChannel(19, "NOVO19", Provider.TF1),
    AbeTntChannel(20, "TF1 Séries Films", Provider.TF1),
    AbeTntChannel(21, "L'Équipe", Provider.TF1),
    AbeTntChannel(22, "6ter", Provider.M6),
    AbeTntChannel(23, "RMC Story", Provider.RMC),
    AbeTntChannel(24, "RMC Découverte", Provider.RMC),
    AbeTntChannel(25, "Chérie 25", Provider.NRJ),
)
