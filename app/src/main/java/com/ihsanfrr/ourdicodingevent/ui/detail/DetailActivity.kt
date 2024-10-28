package com.ihsanfrr.ourdicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.ihsanfrr.ourdicodingevent.data.Result
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.ihsanfrr.ourdicodingevent.R
import com.ihsanfrr.ourdicodingevent.data.local.entity.DicodingEventEntity
import com.ihsanfrr.ourdicodingevent.databinding.ActivityDetailBinding
import com.ihsanfrr.ourdicodingevent.helpers.DateHelper
import com.ihsanfrr.ourdicodingevent.ui.MainViewModel
import com.ihsanfrr.ourdicodingevent.ui.ViewModelFactory
import com.ihsanfrr.ourdicodingevent.ui.setting.SettingPreferences
import com.ihsanfrr.ourdicodingevent.ui.setting.dataStore

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: SettingPreferences
    private lateinit var progressBar: ProgressBar

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this, pref)
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
        progressBar = binding.progressBar
        pref = SettingPreferences.getInstance(this.applicationContext.dataStore)

        if (eventId != null) {
            observeEventDetail(eventId)
        }
    }

    private fun observeEventDetail(eventId: String) {
        viewModel.getDetailEvent(eventId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    updateUI(result.data)
                }

                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(eventDetail: DicodingEventEntity) {

        binding.apply {
            Glide.with(this@DetailActivity)
                .load(eventDetail.imageLogo)
                .into(ivDetailEventImageLogo)

            tvDetailEventName.text = eventDetail.name
            tvDetailEventOwner.text = eventDetail.ownerName
            "${DateHelper.format(eventDetail.beginTime)} - ${DateHelper.format(eventDetail.endTime)}".also {
                tvDetailEventTime.text = it
            }
            "Available Quota: ${eventDetail.quota.minus(eventDetail.registrants)}".also {
                tvDetailEventQuota.text = it
            }
            tvDetailEventDescription.text =
                HtmlCompat.fromHtml(eventDetail.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            btnDetailEventOpenLink.setOnClickListener {
                eventDetail.link.let { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }
                if (eventDetail.link == null) {
                    Toast.makeText(
                        this@DetailActivity,
                        getString(R.string.no_link_found), Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if (eventDetail.isFavorite) {
                btnFavorite.background = AppCompatResources.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_favorite_24
                )
            } else {
                btnFavorite.background = AppCompatResources.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_unfavorite_24
                )
            }

            btnFavorite.setOnClickListener {
                eventDetail.isFavorite = !eventDetail.isFavorite
                binding.btnFavorite.background = if (eventDetail.isFavorite) {
                    AppCompatResources.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_favorite_24
                    )
                } else {
                    AppCompatResources.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_unfavorite_24
                    )
                }

                viewModel.updateFavoriteStatus(eventDetail, eventDetail.isFavorite)

                val message =
                    if (eventDetail.isFavorite) getString(R.string.added_to_favorites) else getString(
                        R.string.removed_from_favorites
                    )
                Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}