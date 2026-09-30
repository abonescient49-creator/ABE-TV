package tv.own.owntv.features.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import tv.own.owntv.ui.components.OwnTVIcon
import tv.own.owntv.ui.theme.OwnTVTheme

/**
 * ABE TV's TV-first home strip.
 *
 * It deliberately does not mimic any broadcaster UI pixel-for-pixel. It uses the familiar
 * "large actions + channel rail" interaction pattern used by modern TV platforms, while keeping
 * OwnTV's focus engine and theme.
 */
@Composable
fun AbeTvQuickHub(
    onOpenLive: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = OwnTVTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.abe_home_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.abe_home_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(R.string.abe_home_badge),
                style = MaterialTheme.typography.labelLarge,
                color = colors.primary,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(18.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                AbeActionCard(
                    title = stringResource(R.string.abe_action_live),
                    subtitle = stringResource(R.string.abe_action_live_subtitle),
                    icon = OwnTVIcon.LIVE_TV,
                    onClick = onOpenLive,
                )
            }
            item {
                AbeActionCard(
                    title = stringResource(R.string.abe_action_guide),
                    subtitle = stringResource(R.string.abe_action_guide_subtitle),
                    icon = OwnTVIcon.EPG,
                    onClick = onOpenGuide,
                )
            }
            item {
                AbeActionCard(
                    title = stringResource(R.string.abe_action_favorites),
                    subtitle = stringResource(R.string.abe_action_favorites_subtitle),
                    icon = OwnTVIcon.FAVORITE,
                    onClick = onOpenFavorites,
                )
            }
            item {
                AbeActionCard(
                    title = stringResource(R.string.abe_action_replay),
                    subtitle = stringResource(R.string.abe_action_replay_subtitle),
                    icon = OwnTVIcon.PLAY,
                    onClick = { launchProvider(context, Provider.FRANCE_TV) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.abe_channels_title),
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurface,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ABE_TNT_CHANNELS, key = { it.number }) { channel ->
                ChannelTile(
                    channel = channel,
                    onClick = {
                        if (channel.provider == Provider.ABE_NATIVE) onOpenLive()
                        else launchProvider(context, channel.provider)
                    },
                )
            }
        }
    }
}

@Composable
private fun AbeActionCard(
    title: String,
    subtitle: String,
    icon: OwnTVIcon,
    onClick: () -> Unit,
) {
    val colors = OwnTVTheme.colors
    FocusableSurface(
        onClick = onClick,
        modifier = Modifier.width(250.dp),
        shape = RoundedCornerShape(18.dp),
        surface = GlassSurface.CARDS,
        focusedContainerColor = colors.primaryContainer,
        unfocusedContainerColor = colors.surfaceContainer,
        contentAlignment = Alignment.CenterStart,
    ) { focused ->
        Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            tv.own.owntv.ui.components.OwnTVIcon(
                icon = icon,
                tint = if (focused) colors.onPrimaryContainer else colors.primary,
                modifier = Modifier.height(23.dp),
            )
            Spacer(Modifier.height(11.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = if (focused) colors.onPrimaryContainer else colors.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (focused) colors.onPrimaryContainer.copy(alpha = 0.78f) else colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChannelTile(channel: AbeChannel, onClick: () -> Unit) {
    val colors = OwnTVTheme.colors
    FocusableSurface(
        onClick = onClick,
        modifier = Modifier.width(138.dp),
        shape = RoundedCornerShape(16.dp),
        surface = GlassSurface.CARDS,
        focusedContainerColor = colors.primaryContainer,
        unfocusedContainerColor = colors.surfaceContainerHigh,
        contentAlignment = Alignment.CenterStart,
    ) { focused ->
        Column(Modifier.padding(horizontal = 14.dp, vertical = 13.dp)) {
            Text(
                text = channel.number.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = if (focused) colors.onPrimaryContainer.copy(alpha = 0.8f) else colors.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = channel.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (focused) colors.onPrimaryContainer else colors.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = providerLabel(channel.provider),
                style = MaterialTheme.typography.labelSmall,
                color = if (focused) colors.onPrimaryContainer.copy(alpha = 0.72f) else colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun providerLabel(provider: Provider): String = when (provider) {
    Provider.ABE_NATIVE -> stringResource(R.string.abe_provider_native)
    Provider.TF1_PLUS -> "TF1+"
    Provider.FRANCE_TV -> "france.tv"
    Provider.M6_PLUS -> "M6+"
    Provider.RMC_PLUS -> "RMC+"
    Provider.CNEWS -> "CNEWS"
    Provider.CANAL -> "CANAL+"
    Provider.T18 -> "T18"
    Provider.NRJ_PLAY -> "NRJ Play"
}

private fun launchProvider(context: Context, provider: Provider) {
    val packages = when (provider) {
        Provider.TF1_PLUS -> listOf("fr.tf1.mytf1")
        Provider.FRANCE_TV -> listOf("fr.francetv.pluzz")
        Provider.M6_PLUS -> listOf("fr.m6.m6replay")
        Provider.RMC_PLUS -> listOf(
            "com.nextinteractive.rmcbfmplay.tv.sfr",
            "com.nextinteractive.rmcbfmplay.tv",
            "com.nextinteractive.rmcbfmplay",
        )
        Provider.CNEWS -> listOf("fr.canalplus.itele")
        Provider.CANAL -> listOf("com.canal.android.canal")
        Provider.ABE_NATIVE, Provider.T18, Provider.NRJ_PLAY -> emptyList()
    }

    packages.firstNotNullOfOrNull { pkg ->
        context.packageManager.getLeanbackLaunchIntentForPackage(pkg)
            ?: context.packageManager.getLaunchIntentForPackage(pkg)
    }?.let { intent ->
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return
    }

    val query = when (provider) {
        Provider.TF1_PLUS -> "TF1+"
        Provider.FRANCE_TV -> "france.tv"
        Provider.M6_PLUS -> "M6+"
        Provider.RMC_PLUS -> "RMC+"
        Provider.CNEWS -> "CNEWS"
        Provider.CANAL -> "CANAL+"
        Provider.T18 -> "T18"
        Provider.NRJ_PLAY -> "NRJ Play"
        Provider.ABE_NATIVE -> "ABE TV"
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

private data class AbeChannel(val number: Int, val name: String, val provider: Provider)

private enum class Provider {
    ABE_NATIVE,
    TF1_PLUS,
    FRANCE_TV,
    M6_PLUS,
    RMC_PLUS,
    CNEWS,
    CANAL,
    T18,
    NRJ_PLAY,
}

private val ABE_TNT_CHANNELS = listOf(
    AbeChannel(1, "TF1", Provider.TF1_PLUS),
    AbeChannel(2, "France 2", Provider.FRANCE_TV),
    AbeChannel(3, "France 3", Provider.FRANCE_TV),
    AbeChannel(4, "France 4", Provider.FRANCE_TV),
    AbeChannel(5, "France 5", Provider.FRANCE_TV),
    AbeChannel(6, "M6", Provider.M6_PLUS),
    AbeChannel(7, "ARTE", Provider.ABE_NATIVE),
    AbeChannel(8, "LCP · Public Sénat", Provider.FRANCE_TV),
    AbeChannel(9, "W9", Provider.M6_PLUS),
    AbeChannel(10, "TMC", Provider.TF1_PLUS),
    AbeChannel(11, "TFX", Provider.TF1_PLUS),
    AbeChannel(12, "Gulli", Provider.M6_PLUS),
    AbeChannel(13, "BFM TV", Provider.RMC_PLUS),
    AbeChannel(14, "CNEWS", Provider.CNEWS),
    AbeChannel(15, "LCI", Provider.TF1_PLUS),
    AbeChannel(16, "franceinfo:", Provider.FRANCE_TV),
    AbeChannel(17, "CStar", Provider.CANAL),
    AbeChannel(18, "T18", Provider.T18),
    AbeChannel(19, "NOVO19", Provider.TF1_PLUS),
    AbeChannel(20, "TF1 Séries Films", Provider.TF1_PLUS),
    AbeChannel(21, "L'Équipe", Provider.TF1_PLUS),
    AbeChannel(22, "6ter", Provider.M6_PLUS),
    AbeChannel(23, "RMC Story", Provider.RMC_PLUS),
    AbeChannel(24, "RMC Découverte", Provider.RMC_PLUS),
    AbeChannel(25, "Chérie 25", Provider.NRJ_PLAY),
)
