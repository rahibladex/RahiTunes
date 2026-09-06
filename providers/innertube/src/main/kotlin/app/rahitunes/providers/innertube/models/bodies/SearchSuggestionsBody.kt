package app.rahitunes.providers.innertube.models.bodies

import app.rahitunes.providers.innertube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class SearchSuggestionsBody(
    val context: Context = Context.DefaultWeb,
    val input: String
)
