package backend.academy.scrapper.repository.jpa.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ChatLinkId implements Serializable{

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "link_id")
    private Long linkId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatLinkId that = (ChatLinkId) o;
        return Objects.equals(chatId, that.chatId) &&
            Objects.equals(linkId, that.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, linkId);
    }
}
