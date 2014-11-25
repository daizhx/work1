package com.jiuzhansoft.ehealthtec.lens.naevus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jiuzhansoft.ehealthtec.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CheckFileExpandListAdapter extends BaseExpandableListAdapter {
	public static final int ItemHeight = 48;
	public static final int PaddingLeft = 36;

	private MyGridView toolbarGrid;

	String path = Environment.getExternalStorageDirectory().toString()
			+ File.separator + "dxlphoto";

	File[] files = new File(path).listFiles();

	private ArrayList<ArrayList<String>> menu_toolbar_name_array = new ArrayList<ArrayList<String>>();

	private List<String> firstName = new ArrayList<String>();

	private HashMap<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>();

	private List<TreeNode> treeNodes = new ArrayList<TreeNode>();

	private Context parentContext;

	private LayoutInflater layoutInflater;

	private ProgressDialog progress;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			progress.dismiss();
		}

	};

	static public class TreeNode {
		Object parent;
		List<Object> childs = new ArrayList<Object>();
	}

	public CheckFileExpandListAdapter(Context view, final List<String> firstName) {
		parentContext = view;

		this.firstName = firstName;
		progress = ProgressDialog.show(parentContext, parentContext
				.getResources().getString(R.string.file), parentContext
				.getResources().getString(R.string.loading));
		new Thread() {
			public void run() {
				for (int i = 0; i < firstName.size(); i++) {
					ArrayList<String> childList = new ArrayList<String>();
					for (int j = 0; j < files.length; j++) {
						if (firstName.get(i).equals(
								files[j].getName().split("_")[0])) {
							if (files[j].getName().split("_").length != 3)
								continue;
							if (files[j].getName().split("_")[1].length() != 19)
								continue;
							if (!files[j].getName().split("_")[2]
									.equals("o.png"))
								continue;
							childList.add(files[j].getName());
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inSampleSize = 32;
							Bitmap bitmap = Bitmap
									.createScaledBitmap(
											BitmapFactory.decodeFile(
													path
															+ File.separator
															+ files[j]
																	.getName(),
													opts), 100, 100, false);
							bitmap = getRoundedCornerBitmap(bitmap, 20);
							bitmapMap.put(
									path + File.separator + files[j].getName(),
									bitmap);
						}
					}
					menu_toolbar_name_array.add(childList);
				}
				handler.sendEmptyMessage(0);
			}
		}.start();

	}

	public Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap.recycle();
		return output;
	}

	public List<TreeNode> GetTreeNode() {
		return treeNodes;
	}

	public void UpdateTreeNode(List<TreeNode> nodes) {
		treeNodes = nodes;
	}

	public void RemoveAll() {
		treeNodes.clear();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return treeNodes.get(groupPosition).childs.get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return treeNodes.get(groupPosition).childs.size();
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		layoutInflater = (LayoutInflater) parentContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.expand_child_grid, null);
		// convertView.setBackgroundColor(Color.rgb(10, 60, 100));
		toolbarGrid = (MyGridView) convertView.findViewById(R.id.expand_grid);
		toolbarGrid.setNumColumns(3);
		toolbarGrid.setGravity(Gravity.CENTER);
		toolbarGrid.setHorizontalSpacing(10);
		toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array
				.get(groupPosition)));
		toolbarGrid.setOnItemClickListener(new onExpandItemClickListener(
				groupPosition + ""));
		toolbarGrid
				.setOnItemLongClickListener(new onExpandItemLongClickListener(
						groupPosition + ""));
		return convertView;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		layoutInflater = (LayoutInflater) parentContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.expand_group, null);
		TextView textView = (TextView) convertView.findViewById(R.id.textGroup);
		textView.setText(getGroup(groupPosition).toString());
		textView.setPadding(0, 20, 0, 20);
		textView.setTextSize(25);
		textView.setTextColor(Color.WHITE);
		textView.setBackgroundColor(Color.rgb(0, 110, 180));
		return convertView;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getGroup(int groupPosition) {
		return treeNodes.get(groupPosition).parent;
	}

	public int getGroupCount() {
		return treeNodes.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	private ImageAdapter getMenuAdapter(ArrayList<String> menuNameArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", path + File.separator + menuNameArray.get(i));
			map.put("itemText", menuNameArray.get(i).split("_")[1]);
			data.add(map);
		}
		ImageAdapter imageAdapter = new ImageAdapter(parentContext, data,
				R.layout.grid_item, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return imageAdapter;
	}

	public class ImageAdapter extends SimpleAdapter {
		public ImageAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void setViewImage(ImageView v, String value) {
			// TODO Auto-generated method stub

			v.setImageBitmap(bitmapMap.get(value));

		}

		@Override
		public void setViewText(TextView v, String text) {
			// TODO Auto-generated method stub
			v.setGravity(Gravity.CENTER);
//			String newText = text.substring(0, 10) + "\n"
//					+ text.substring(11, 16);
//			text = text.replace(" ", "\n");
			text = text.replaceFirst(" ", "\n");
			text = text.replaceFirst(" ", ":");
			String newText = text.substring(0, 16);
			v.setText(newText);
//			v.setTextColor(Color.WHITE);
		}

	}

	public class onExpandItemClickListener implements OnItemClickListener {

		private String name;

		public onExpandItemClickListener(String name) {
			this.name = name;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(parentContext, FileDataActivity.class);
			intent.putExtra(
					"o",
					path
							+ File.separator
							+ menu_toolbar_name_array.get(
									Integer.parseInt(name)).get(arg2));
			parentContext.startActivity(intent);
		}

	}

	public class onExpandItemLongClickListener implements
			OnItemLongClickListener {

		private String name;

		public onExpandItemLongClickListener(String name) {
			this.name = name;
		}

		@Override
		public boolean onItemLongClick(final AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			final AlertDialog alertdialog = (new AlertDialog.Builder(
					parentContext)).create();

			final int pos = position;
			alertdialog.setMessage(parentContext.getResources().getString(
					R.string.whether_delete_file));
			alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, parentContext
					.getResources().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

							// TODO Auto-generated method stub
							String filename = path
									+ File.separator
									+ menu_toolbar_name_array.get(
											Integer.parseInt(name)).get(pos);
							File fileo = new File(filename);
							if (fileo.exists()) {
								if (fileo.isFile()) {
									fileo.delete();
								}
							}
							File filea = new File(filename.substring(0,
									filename.length() - 5) + "a.png");
							if (filea.exists()) {
								if (filea.isFile()) {
									filea.delete();
								}
							}
							menu_toolbar_name_array.get(Integer.parseInt(name))
									.remove(pos);
							bitmapMap.remove(filename);
							((MyGridView) parent)
									.setAdapter(getMenuAdapter(menu_toolbar_name_array
											.get(Integer.parseInt(name))));
							if (menu_toolbar_name_array.get(
									Integer.parseInt(name)).size() == 0) {
								menu_toolbar_name_array.remove(Integer
										.parseInt(name));
								treeNodes.remove(Integer.parseInt(name));
							}
							notifyDataSetChanged();
							alertdialog.dismiss();

						}
					});
			alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, parentContext
					.getResources().getString(R.string.no),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertdialog.cancel();
						}
					});
			alertdialog.show();

			return false;
		}

	}
}