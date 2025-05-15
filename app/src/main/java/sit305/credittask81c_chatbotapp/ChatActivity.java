package sit305.credittask81c_chatbotapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sit305.credittask81c_chatbotapp.api.APIClient;
import sit305.credittask81c_chatbotapp.api.GemmaAPI;
import sit305.credittask81c_chatbotapp.models.GemmaRequest;
import sit305.credittask81c_chatbotapp.models.GemmaResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        });
    }

    // Send user message and receive bot response
    private void sendMessage(String message) {
        // Add user message immediately
        addMessage(message, true);

        messageEditText.setText("");

        // Show bot is typing
        addMessage("Typing...", false);

        // Send message to bot
        GemmaAPI apiService = APIClient.getClient().create(GemmaAPI.class);
        GemmaRequest request = new GemmaRequest(message);

        apiService.sendMessage(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GemmaResponse> call, Response<GemmaResponse> response) {
                removeTypingIndicator();

                if (response.isSuccessful() && response.body() != null) {
                    String botReply = response.body().getGeneratedText();
                    addMessage(botReply, false);
                } else {
                    Log.e("ChatActivity", "Response failed. Code: " + response.code());
                    try {
                        Log.e("ChatActivity", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("ChatActivity", "Error reading errorBody", e);
                    }
                    addMessage("Failed to get a response. Please try again.", false);
                }
            }

            @Override
            public void onFailure(Call<GemmaResponse> call, Throwable t) {
                removeTypingIndicator();
                Log.e("ChatActivity", "Network failure: ", t);
                addMessage("Error: " + t.getMessage(), false);
            }
        });
    }

    // Add message to the list and update UI
    private void addMessage(String message, boolean isUser) {
        chatMessages.add(new ChatMessage(message, isUser));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    // Remove "Typing..." indicator
    private void removeTypingIndicator() {
        if (!chatMessages.isEmpty() && chatMessages.get(chatMessages.size() - 1).getMessage().equals("Typing...")) {
            chatMessages.remove(chatMessages.size() - 1);
            chatAdapter.notifyItemRemoved(chatMessages.size());
        }
    }

}
