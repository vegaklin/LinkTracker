package backend.academy.scrapper.repository.jpa.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class ChatLinkId implements Serializable {

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "link_id")
    private Long linkId;
}
