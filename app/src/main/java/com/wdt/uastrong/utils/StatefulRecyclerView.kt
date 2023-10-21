package com.wdt.uastrong.utils


import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.os.Parcelable
import android.os.Bundle
import android.util.AttributeSet

class StatefulRecyclerView : RecyclerView {
    private var mLayoutManagerSavedState: Parcelable? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    )
    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState())
        bundle.putParcelable(
            SAVED_LAYOUT_MANAGER, this.layoutManager?.onSaveInstanceState()
        )
        return bundle
    }

    public override fun onRestoreInstanceState(parceState: Parcelable) {
        var state: Parcelable? = parceState
        if (state is Bundle) {
            val bundle = state
            mLayoutManagerSavedState = bundle.getParcelable(SAVED_LAYOUT_MANAGER)
            state = bundle.getParcelable(SAVED_SUPER_STATE)
        }
        super.onRestoreInstanceState(state)
    }

    /**
     * Restores scroll position after configuration change.
     *
     *
     * **NOTE:** Must be called after adapter has been set.
     */
    private fun restorePosition() {
        if (mLayoutManagerSavedState != null) {
            this.layoutManager?.onRestoreInstanceState(mLayoutManagerSavedState)
            mLayoutManagerSavedState = null
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        restorePosition()
    }

    companion object {
        private const val SAVED_SUPER_STATE = "super-state"
        private const val SAVED_LAYOUT_MANAGER = "layout-manager-state"
    }
}