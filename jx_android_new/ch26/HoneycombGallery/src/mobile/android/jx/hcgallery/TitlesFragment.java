
package mobile.android.jx.hcgallery;



import android.app.ListFragment;
import android.content.ClipData;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TitlesFragment extends ListFragment {
    private int mCategory = 0;
    private int mCurPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       
        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getInt("category");
            mCurPosition = savedInstanceState.getInt("listPosition");
        }

        populateTitles(mCategory);
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setCacheColorHint(Color.TRANSPARENT);
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                final String title = (String) ((TextView) v).getText();

              
                final String textData = String.format("%d||%d", mCategory, pos);
                ClipData data = ClipData.newPlainText(title, textData);
                v.startDrag(data, new MyDragShadowBuilder(v), null, 0);
                return true;
            }
        });

        selectPosition(mCurPosition);
    }

    private class MyDragShadowBuilder extends View.DragShadowBuilder {
        private Drawable mShadow;

        public MyDragShadowBuilder(View v) {
            super(v);

            final TypedArray a = v.getContext().obtainStyledAttributes(R.styleable.AppTheme);
            mShadow = a.getDrawable(R.styleable.AppTheme_listDragShadowBackground);
            mShadow.setCallback(v);
            mShadow.setBounds(0, 0, v.getWidth(), v.getHeight());
            a.recycle();
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            super.onDrawShadow(canvas);
            mShadow.draw(canvas);
            getView().draw(canvas);
        }
    }

    public void populateTitles(int category) {
        DirectoryCategory cat = Directory.getCategory(category);
        String[] items = new String[cat.getEntryCount()];
        for (int i = 0; i < cat.getEntryCount(); i++)
            items[i] = cat.getEntry(i).getName();
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.title_list_item, items));
        mCategory = category;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        updateImage(position);
    }

    private void updateImage(int position) {
        ContentFragment frag = (ContentFragment) getFragmentManager()
                .findFragmentById(R.id.frag_content);
        frag.updateContentAndRecycleBitmap(mCategory, position);
        mCurPosition = position;
    }

    public void selectPosition(int position) {
        ListView lv = getListView();
        lv.setItemChecked(position, true);
        updateImage(position);
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("listPosition", mCurPosition);
        outState.putInt("category", mCategory);
    }
}
