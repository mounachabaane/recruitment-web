package fr.d2factory.libraryapp.book;

import fr.d2factory.libraryapp.member.Member;

/**
 * A simple representation of a book
 */
public class Book {
	private String title;
	private String author;
	private ISBN isbn;
	private Member member;

	public Book() {
	}

	public Book(String title, String author, ISBN isbn) {
		super();
		this.title = title;
		this.author = author;
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ISBN getIsbn() {
		return isbn;
	}

	public void setIsbn(ISBN isbn) {
		this.isbn = isbn;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@Override
	public String toString() {
		return "Book [title=" + title + ", author=" + author + ", isbn=" + isbn + ", member=" + member + "]";
	}

}
