/*
 * Copyright 2010 - Brion Noble Emde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eyebrowssoftware.bptrackerfree.fragments;

import junit.framework.Assert;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.eyebrowssoftware.bptrackerfree.BPRecords.BPRecord;
import com.eyebrowssoftware.bptrackerfree.BPTrackerFree;
import com.eyebrowssoftware.bptrackerfree.R;
import com.eyebrowssoftware.bptrackerfree.RangeAdapter;
import com.eyebrowssoftware.bptrackerfree.fragments.BPRecordEditorFragment.EditorPlugin;

public class EditorSpinnerFragment extends Fragment implements EditorPlugin, LoaderCallbacks<Cursor> {
    static final String TAG = "EditorSpinnerFragment";

    private static final String URI_KEY = "uri_key";

    public static EditorSpinnerFragment newInstance(Uri uri) {
        EditorSpinnerFragment fragment = new EditorSpinnerFragment();
        Bundle args = new Bundle();
        args.putString(URI_KEY, uri.toString());
        fragment.setArguments(args);
        return fragment;
    }

    private static final int SPINNER_EDITOR_LOADER_ID = 2356;

    private static final int[] SYSTOLIC_RANGE_SETUP = {
        BPTrackerFree.SYSTOLIC_MAX_DEFAULT,
        RangeAdapter.NO_ZONE,
        RangeAdapter.NO_ZONE,
        RangeAdapter.NO_ZONE,
        BPTrackerFree.SYSTOLIC_MIN_DEFAULT
    };

    private static final int[] DIASTOLIC_RANGE_SETUP = {
        BPTrackerFree.DIASTOLIC_MAX_DEFAULT,
        RangeAdapter.NO_ZONE,
        RangeAdapter.NO_ZONE,
        RangeAdapter.NO_ZONE,
        BPTrackerFree.DIASTOLIC_MIN_DEFAULT
    };

    private static final int[] PULSE_RANGE_SETUP = {
        BPTrackerFree.PULSE_MAX_DEFAULT,
        RangeAdapter.NO_ZONE,
        RangeAdapter.NO_ZONE,
        RangeAdapter.NO_ZONE,
        BPTrackerFree.PULSE_MIN_DEFAULT
    };

    private Spinner mSystolic;
    private Spinner mDiastolic;
    private Spinner mPulse;

    protected static final int SPINNER_ITEM_RESOURCE_ID = R.layout.bp_spinner_item;
    protected static final int SPINNER_ITEM_TEXT_VIEW_ID = android.R.id.text1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spinners_fragment, container, false);

        mSystolic = (Spinner) v.findViewById(R.id.systolic_spinner);
        mSystolic.setPromptId(R.string.label_sys_spinner);

        mDiastolic = (Spinner) v.findViewById(R.id.diastolic_spinner);
        mDiastolic.setPromptId(R.string.label_dia_spinner);

        mPulse = (Spinner) v.findViewById(R.id.pulse_spinner);
        mPulse.setPromptId(R.string.label_pls_spinner);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle icicle) {
        super.onActivityCreated(icicle);

        Activity activity = getActivity();
        mSystolic.setAdapter(new RangeAdapter(activity, SYSTOLIC_RANGE_SETUP, true, SPINNER_ITEM_RESOURCE_ID, SPINNER_ITEM_TEXT_VIEW_ID));
        mDiastolic.setAdapter(new RangeAdapter(activity, DIASTOLIC_RANGE_SETUP, true, SPINNER_ITEM_RESOURCE_ID, SPINNER_ITEM_TEXT_VIEW_ID));
        mPulse.setAdapter(new RangeAdapter(activity, PULSE_RANGE_SETUP, true, SPINNER_ITEM_RESOURCE_ID, SPINNER_ITEM_TEXT_VIEW_ID));
        this.getActivity().getSupportLoaderManager().initLoader(SPINNER_EDITOR_LOADER_ID, null, this);
    }

    private void setSpinner(Spinner s, int value) {
        RangeAdapter sa = (RangeAdapter) s.getAdapter();
        Assert.assertNotNull(sa);
        s.setSelection(sa.getPosition(value));
        sa.notifyDataSetChanged();
    }

    @Override
    public void updateCurrentValues(ContentValues values) {
        values.put(BPRecord.SYSTOLIC, (Integer) mSystolic.getSelectedItem());
        values.put(BPRecord.DIASTOLIC, (Integer) mDiastolic.getSelectedItem());
        values.put(BPRecord.PULSE, (Integer) mPulse.getSelectedItem());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = Uri.parse(getArguments().getString(URI_KEY));
        return new CursorLoader(this.getActivity(), uri, BPTrackerFree.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Assert.assertNotNull(cursor);
        if (loader.getId() == SPINNER_EDITOR_LOADER_ID && cursor.moveToFirst()) {
            this.setSpinner(mSystolic, cursor.getInt(BPTrackerFree.COLUMN_SYSTOLIC_INDEX));
            this.setSpinner(mDiastolic, cursor.getInt(BPTrackerFree.COLUMN_DIASTOLIC_INDEX));
            this.setSpinner(mPulse, cursor.getInt(BPTrackerFree.COLUMN_PULSE_INDEX));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // Nada
    }
}
