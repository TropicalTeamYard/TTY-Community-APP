package tty.community.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import tty.community.pages.fragment.CreateBlogComplexFragment
import tty.community.pages.fragment.CreateBlogProFragment
import tty.community.pages.fragment.CreateBlogShortFragment

class CreateBlogFragmentAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var pages: ArrayList<Fragment> = ArrayList()

    init {
        pages.add(CreateBlogShortFragment())
        pages.add(CreateBlogComplexFragment())
        pages.add(CreateBlogProFragment())
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun getItem(p0: Int): Fragment {
        return pages[p0]
    }

}