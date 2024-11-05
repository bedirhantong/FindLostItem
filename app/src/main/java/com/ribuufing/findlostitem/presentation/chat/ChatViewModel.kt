package com.ribuufing.findlostitem.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ribuufing.findlostitem.data.model.LostItem
import com.ribuufing.findlostitem.data.model.Message
import com.ribuufing.findlostitem.data.model.User
import com.ribuufing.findlostitem.domain.use_cases.GetLostItemByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getLostItemByIdUseCase: GetLostItemByIdUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _lostItem = MutableStateFlow(LostItem())
    val lostItem = _lostItem

    // Yeni mesaj ekleme fonksiyonu
    fun addMessage(message: Message) {
        viewModelScope.launch {
            _messages.update { currentMessages ->
                currentMessages + message // Eski mesajların sonuna ekle
            }
        }
    }

    fun getLostItemById(itemId: String) {
        viewModelScope.launch {
            _lostItem.value = getLostItemByIdUseCase.invoke(itemId)
        }
    }

    // Dummy veri eklemek için (test amaçlı)
    fun addDummyData() {
        viewModelScope.launch {
            _messages.update {
                listOf(
                    Message(
                        id = 1,
                        senderUser = User("1", "İbrahim Serhan Baymaz", imageUrl = "https://avatars.githubusercontent.com/u/102352030?v=4"),
                        receiverUser = User("2", "Bedirhan Tong", imageUrl = "https://avatars.githubusercontent.com/u/70720131?v=4"),
                        content = "Hi, you found my laptop. Can we meet?",
                        date = "1/15 at 2pm"
                    ),
                    Message(
                        id = 2,
                        senderUser = User("2", "Bedirhan Tong", imageUrl = "https://avatars.githubusercontent.com/u/70720131?v=4"),
                        receiverUser = User("1", "İbrahim Serhan Baymaz", imageUrl = "https://avatars.githubusercontent.com/u/102352030?v=4"),
                        content = "I have a course now, let's meet at 4pm.",
                        date = "1/15 at 2:10pm"
                    )
                )
            }
        }
    }
}
