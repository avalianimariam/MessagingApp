package ge.gabramishvilimavaliani.messagingapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import ge.gabramishvilimavaliani.messagingapp.R
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment() {

    var root: ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chat, container, false) as ViewGroup?

        val activity = activity as AppCompatActivity?
        val toolbar: Toolbar = root!!.findViewById(ge.gabramishvilimavaliani.messagingapp.R.id.toolbar)
        activity!!.setSupportActionBar(toolbar)

        activity.supportActionBar!!.setHomeButtonEnabled(true)
        collapsingToolbar()

        return root
    }

    fun collapsingToolbar() {

        val collapsingToolbarLayout = root!!.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        val app_bar: AppBarLayout = root!!.findViewById(R.id.appbar)

        app_bar.addOnOffsetChangedListener(object :
            AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = false
                } else {
                    collapsingToolbarLayout.title = ""
                    isShow = true
                }
            }

        })
    }

}