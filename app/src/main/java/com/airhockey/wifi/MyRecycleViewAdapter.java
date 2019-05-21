package com.airhockey.wifi;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.airhockey.android.R;
import com.airhockey.wifi.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by likedong on 2018/1/15.
 */


public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder> {
    private List<ScanResult> list;
    OnItemClickListener listener;
    private String connectSSID;
    public MyRecycleViewAdapter(List<ScanResult> list) {
        this.list = list;
    }

    @Override
    public MyRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal, parent, false);
        MyRecycleViewAdapter.ViewHolder viewHolder = new MyRecycleViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    public void setConnectSSID(String connectSSID){
        this.connectSSID = connectSSID;
    }
    @Override
    public void onBindViewHolder(MyRecycleViewAdapter.ViewHolder holder, int position) {
        ScanResult data = list.get(position);
        holder.mText.setText(data.SSID);
        holder.mText.setTag(data.SSID);
        holder.mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position,holder.mState,holder);
            }
        });

        if(data.SSID.equals(connectSSID)){
            holder.mState.setText("已连接");
        } else
            holder.mState.setText("");
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mText;
        TextView mState;
        ViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.item_tx);
            mState = itemView.findViewById(R.id.item_state);
        }
    }
}
