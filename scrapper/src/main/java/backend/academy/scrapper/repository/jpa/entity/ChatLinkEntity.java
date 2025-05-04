package backend.academy.scrapper.repository.jpa.entity;

import backend.academy.scrapper.repository.jpa.entity.model.ChatLinkId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "chat_links")
public class ChatLinkEntity {

    @EmbeddedId
    private ChatLinkId id;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private ChatEntity chat;

    @ManyToOne
    @MapsId("linkId")
    @JoinColumn(name = "link_id", referencedColumnName = "id")
    private LinkEntity link;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags")
    private List<String> tags;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "filters")
    private List<String> filters;

    public Long getChatId() {
        return id.chatId();
    }

    public Long getLinkId() {
        return id.linkId();
    }
}
