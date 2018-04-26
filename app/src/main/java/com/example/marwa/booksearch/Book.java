package com.example.marwa.booksearch;

/**
 * Created by Marwa on 11/5/2017.
 */

public class Book {

    /**
     * Title of the book
     */
    private String title;
    /**
     * Author of the book
     */
    private String author;
    /**
     * Image resource for the book
     */
    private String imageResource;
    /**
     * Description of the book
     */
    private String description;

    /**
     * create a new {@link Book} object;
     *
     * @param title         is title of the book.
     * @param author        is author of the book.
     * @param imageResource is the cover of the book
     * @param description   is description of the book*
     */
    Book(String title, String author, String imageResource, String description) {
        this.title = title;
        this.author = author;
        this.imageResource = imageResource;
        this.description = description;
    }

    /**
     * return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Return the image resource ID of the book.
     */
    public String getImageResource() {
        return imageResource;
    }

    /**
     * Returns whether or not there is an image for this book.
     */
    public boolean hasImage() {
        if (getImageResource().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * return the author of the book
     */
    public String getDescription() {
        return description;
    }

}
