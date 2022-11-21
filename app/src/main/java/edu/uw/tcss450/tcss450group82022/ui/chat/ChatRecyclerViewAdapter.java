package edu.uw.tcss450.tcss450group82022.ui.chat;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatMessageBinding;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder> {

    private final List<ChatMessage> mMessages;
    private final String mEmail;
    public ChatRecyclerViewAdapter(List<ChatMessage> messages, String email) {
        this.mMessages = messages;
        mEmail = email;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.setMessage(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentChatMessageBinding binding;

        public MessageViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentChatMessageBinding.bind(view);
        }

        void setMessage(final ChatMessage message) {
            final Resources res = mView.getContext().getResources();
            final MaterialCardView card = binding.cardRoot;

            int standard = (int) res.getDimension(R.dimen.chat_margin);
            int extended = (int) res.getDimension(R.dimen.chat_margin_sided);

            if (mEmail.equals(message.getSender())) {
                //This message is from the user. Format it as such
                binding.textMessage.setText(message.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                //Set the left margin
                layoutParams.setMargins(extended, standard, standard, standard);
                // Set this View to the right (end) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.END;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                            res.getColor(R.color.primaryLightColor, null),
                            16));
                binding.textMessage.setTextColor(
                        res.getColor(R.color.secondaryTextColorFade, null));

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(ColorUtils.setAlphaComponent(
                        res.getColor(R.color.primaryLightColor, null),
                        200));

                //Round the corners on the left side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCornerSize(0)
                                .setTopRightCornerSize(0)
                                .build());

                card.requestLayout();
            } else {
                //This message is from another user. Format it as such
                binding.textMessage.setText(message.getSender() +
                        ": " + message.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                //Set the right margin
                layoutParams.setMargins(standard, standard, extended, standard);
                // Set this View to the left (start) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.START;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                                res.getColor(R.color.secondaryLightColor, null),
                                16));

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(ColorUtils.setAlphaComponent(
                        res.getColor(R.color.secondaryLightColor, null),
                        200));

                binding.textMessage.setTextColor(
                        res.getColor(R.color.secondaryTextColorFade, null));

                //Round the corners on the right side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCornerSize(0)
                                .setTopLeftCornerSize(0)
                                .build());
                card.requestLayout();
            }
        }
    }
}
