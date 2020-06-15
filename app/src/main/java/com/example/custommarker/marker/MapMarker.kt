package com.example.custommarker.marker

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.drawToBitmap
import com.example.custommarker.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.android.synthetic.main.custom_map_marker.view.*

class MapMarker constructor(
    private val rootViewGroup: ViewGroup,
    type: Type = Type.Destination(),
    state: State = State.Dropped,
    onClickListener: (() -> Unit)? = null
) {

    sealed class Type {
        class Destination(val title: String? = null, val subTitle: String? = null) : Type()
        class DestinationLeft(val eta: String? = null) : Type()
    }

    sealed class State {
        object Expanded : State()
        object ExpandedLeft : State()
        object Dropped : State()
        object Loading : State()
        object Moving : State()
    }

    private val markerView: MarkerView = MarkerView(rootViewGroup, type, state, onClickListener)

    var state: State = state
        set(value) {
            field = value
            markerView.state = value
        }

    var type = type
        set(value) {
            field = value
            markerView.type = type
        }

    var onClickListener: (() -> Unit)? = onClickListener
        set(value) {
            field = value
            markerView.onClickListener = value
        }

    fun remove() {
        rootViewGroup.removeView(markerView)
    }

    fun getGoogleMapsMarker(): BitmapDescriptor? {
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        val bitmap = markerView.drawToBitmap(Bitmap.Config.ARGB_8888)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

private class MarkerView(
    rootViewGroup: ViewGroup,
    type: MapMarker.Type,
    state: MapMarker.State,
    onClickListener: (() -> Unit)?
) : RelativeLayout(rootViewGroup.context) {

    private lateinit var view: View


    var type: MapMarker.Type = type
        set(value) {
            if (type == value) {
                return
            }
            field = value
            setIndicatorType()
        }

    var state: MapMarker.State = state
        set(value) {
            if (state == value) {
                return
            }
            field = value
            setState()
        }

    var onClickListener: (() -> Unit)? = onClickListener
        set(value) {
            field = value
            setListener()
        }

    var onMeasured: (() -> Unit)? = null

    init {
        initLayoutParams()
        inflate()
        measure()
        setProperties()
    }

    private fun initLayoutParams() {
        layoutParams = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    private fun measure() {
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    }

    private fun inflate() {
        view = View.inflate(context, R.layout.custom_map_marker, this)
    }

    private fun setProperties() {
        setIndicatorType()
        setListener()
        setState()
    }

    private fun setIndicatorType() {
        when (type) {
            is MapMarker.Type.Destination -> {
                val markerType = type as MapMarker.Type.Destination
                updateInfo(markerType.title, markerType.subTitle)
            }
            is MapMarker.Type.DestinationLeft -> {
                iv_marker.setImageResource(R.drawable.ic_pin_drop_black_24dp)
                val markerType = type as MapMarker.Type.DestinationLeft
                updateEtaInfo(markerType.eta)
            }
        }
    }

    private fun updateEtaInfo(eta: String?) {
        eta?.let { tv_eta.text = it }
    }

    private fun updateInfo(title: String?, subTitle: String?) {
        title?.let { tv_title.text = it }
        subTitle?.let { tvSubTitle.text = it }
    }


    private fun setState() {
        when (state) {
            is MapMarker.State.Expanded -> expand()
            is MapMarker.State.ExpandedLeft -> expandLeft()
            MapMarker.State.Dropped -> drop()
            MapMarker.State.Loading -> load()
            MapMarker.State.Moving -> move()
        }
    }

    private fun setListener() {
        if (onClickListener != null) {
            cl_info.setOnClickListener { onClickListener?.invoke() }
        }
        else {
            cl_info.isClickable = false
            cl_info.isFocusable = false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        onMeasured?.invoke()
    }

    private fun expandLeft() {
        cl_info_left.visibility = View.VISIBLE
    }

    private fun expand() {
        cl_info.visibility = View.VISIBLE
    }

    private fun drop() {
        cl_info.visibility = View.GONE
    }

    private fun load() {}
    private fun move() {}
}
