package sample.message;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import sample.user.User;

/**
 * @author Rob Winch
 */
@Document
public class Message {
	@Id
	private Long id;

	@DBRef
	private User to;

	@DBRef
	private User from;

	private String text;

	public Message() {}

	public Message(Long id, User to, User from, String text) {
		this.id = id;
		this.to = to;
		this.from = from;
		this.text = text;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getTo() {
		return this.to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public User getFrom() {
		return this.from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
