package com.lsz.viewpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ViewPager viewPager;
	private TextView descTv;
	private LinearLayout pointLayout;
	private int imgs[];
	private List<ImageView> imgViews;
	private String[] imgDesc;
	private int previous = 0; // 当前的圆点
	private Handler handler;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager   = (ViewPager) findViewById(R.id.viewPager);
		descTv      = (TextView) findViewById(R.id.describe_tv);
		pointLayout = (LinearLayout) findViewById(R.id.point_layout);
		initData();
		viewPager.setAdapter(new MyPagerAdapter());
		int tmp = Integer.MAX_VALUE / 2;                   // 从中间开始播放，以便循环播放
		tmp -= tmp % imgViews.size();                      // 保证最开始显示的是第一个圆点
		viewPager.setCurrentItem(tmp);                     // 设置开始item
		viewPager.setOnPageChangeListener(pageListener);   // 设置页面变化监听器
		autoPlay();
	}

	private void initData() {
		imgs = new int[] { R.drawable.a, R.drawable.b, R.drawable.c,R.drawable.d, R.drawable.e };
		imgViews = new ArrayList<ImageView>();

		View pointView = null;
		ImageView imageView = null;
		for (int i = 0; i < imgs.length; i++) {
			LayoutParams params = new LayoutParams(8, 8);  // 设置圆点的宽高
					     params.leftMargin = 6;            // 设置点与点之间的间距
			pointView = new View(this);
			pointView.setLayoutParams(params);
			pointView.setBackgroundResource(R.drawable.point_select);
			pointView.setEnabled(i == 0 ? true : false);
			pointLayout.addView(pointView);

			imageView = new ImageView(this);
			imageView.setBackgroundResource(imgs[i]);
			imgViews.add(imageView);
		}
		imgDesc = new String[] { "Java", "Android", "Linux", "Python", "C/C++" };
	}

	/**
	 * 自动播放图片
	 */
	private void autoPlay() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);  
					System.out.println("MainActivity.onCreate(...).new Handler() {...}.handleMessage()");
				}
			}
		};

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0x123);
			}
		}, 0, 2000);
	}

	@Override
	protected void onDestroy() {
		timer.cancel(); // 取消定时任务，防止页面关闭还在播放图片
		super.onDestroy();
	}

	/**
	 * ViewPager适配器
	 */
	private class MyPagerAdapter extends PagerAdapter {
		/**
		 * 显示Item数量
		 */
		@Override
		public int getCount() {
			// return imgViews == null ? 0 : imgViews.size();
			return Integer.MAX_VALUE;
		}

		/**
		 * 对象是否可复用
		 */
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		/**
		 * 当前显示的Item对象
		 * 
		 * @param container
		 *            : 其实就是ViewPager
		 * @param position
		 *            : 当前Item位置
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// viewPager.addView(imgViews.get(position));
			container.addView(imgViews.get(position % imgViews.size())); // 由于container就是ViewPager,
																		 // 所以上面的效果一样
			return imgViews.get(position % imgViews.size());
		}

		/**
		 * 销毁Item
		 * 
		 * @param container
		 *            : 就是ViewPager
		 * @param position
		 *            : 当前Item位置
		 * @param object
		 *            : 要销毁的Item
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// viewPager.removeView(imgViews.get(position));
			container.removeView((View) object); // 从ViewPager中删除该Item
		}
	}

	/**
	 * ViewPager滚动事件监听器
	 */
	OnPageChangeListener pageListener = new OnPageChangeListener() {

		/**
		 * 选中监听方法
		 */
		@Override
		public void onPageSelected(int position) {
			descTv.setText(imgDesc[position % imgViews.size()]); // 设置图片描述
			pointLayout.getChildAt(position % imgViews.size()).setEnabled(true); // 更改当前圆点状态
			pointLayout.getChildAt(previous % imgViews.size()).setEnabled(false); // 更改上一次圆点状态
			previous = position % imgViews.size(); // 记录当前位置
		}

		/**
		 * 滚动监听方法
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset,int positionOffsetPixels) {

		}

		/**
		 * 状态改变的时候调用 state : 1 正在滑动 , 2 滑动完毕 , 0 什么都没做
		 */
		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

}
