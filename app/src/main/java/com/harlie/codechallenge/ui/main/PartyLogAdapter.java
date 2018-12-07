package com.harlie.codechallenge.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.harlie.codechallenge.BR;
import com.harlie.codechallenge.BaseActivity;
import com.harlie.codechallenge.databinding.PartyLogItemBinding;
import com.harlie.codechallenge.model.PartyLogItem;
import com.harlie.codechallenge.service.PartyLogReaderService;
import com.harlie.codechallenge.viewmodel.PartyLogViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.harlie.codechallenge.viewmodel.PartyLogViewModel.MAX_LOG_SIZE;


public class PartyLogAdapter extends RecyclerView.Adapter<PartyLogAdapter.PartyLogViewHolder> {
    private static final String TAG = "LEE: " + PartyLogAdapter.class.getSimpleName();

    private final BaseActivity mBaseActivity;
    private final PartyLogViewModel mPartyLogViewModel;
    private List<PartyLogItem> mPartyLogItems = new ArrayList<>();

    public PartyLogAdapter(BaseActivity baseActivity, PartyLogViewModel partyLogViewModel) {
        this.mBaseActivity = baseActivity;
        this.mPartyLogViewModel = partyLogViewModel;
    }

    @NonNull
    @Override
    public PartyLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final PartyLogItemBinding partyLogItemBinding = PartyLogItemBinding.inflate(layoutInflater, parent, false);
        partyLogItemBinding.setView(mBaseActivity);
        partyLogItemBinding.setPresenter(mPartyLogViewModel);
        PartyLogViewHolder partyLogViewHolder = new PartyLogViewHolder(partyLogItemBinding);
        return partyLogViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PartyLogViewHolder partyLogViewHolder, int position) {
        Object obj = mPartyLogItems.get(position);
        partyLogViewHolder.bind(obj);
    }

    @Override
    public int getItemCount() {
        return mPartyLogItems.size();
    }

    public void setPartyLogItems(List<PartyLogItem> partyLogItems) {
        Log.d(TAG, "setPartyLogItems");
        this.mPartyLogItems = partyLogItems;
        notifyDataSetChanged();
/*
        // optimize notification
        int inserted = (mPartyLogItems.size() - PartyLogReaderService.getBATCH_SIZE());
        if (mPartyLogItems.size() >= MAX_LOG_SIZE) {
            notifyItemRangeRemoved(0, PartyLogReaderService.getBATCH_SIZE());
        }
        if (inserted > 0) {
            notifyItemRangeInserted(inserted, PartyLogReaderService.getBATCH_SIZE());
        }
*/
    }


    class PartyLogViewHolder extends RecyclerView.ViewHolder {
        private final PartyLogItemBinding binding;

        public PartyLogViewHolder(@NonNull PartyLogItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj) {
            binding.setVariable(BR.obj, obj);
            binding.executePendingBindings();
        }
    }
}
