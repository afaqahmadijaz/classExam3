import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Library lib = new Library();

        Book b1 = new Book("Meditations", "Marcus Aurelius");
        Book b2 = new Book("The 7 Habits of Highly Effective People", "Stephen Covey");
        Movie m1 = new Movie("Interstellar", 169);
        Movie m2 = new Movie("The Pursuit of Happyness", 117);

        lib.addToCatalog(b1);
        lib.addToCatalog(m1);
        lib.addToCatalog(b2);
        lib.addToCatalog(m2);

        lib.setFeaturedItem(0, b1);
        lib.setFeaturedItem(1, m2);

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a title to search: ");
        String title = sc.nextLine();

        if (lib.searchRecursive(title)) {
            System.out.println("Found");
        }
        else {
            System.out.println("Not found");
        }

        for (MediaItem item : lib.getFeaturedShelf()) {
            if (item != null) item.play();
        }

        for (MediaItem item : lib.getCatalog()) {
            item.play();
        }
    }
}

class MediaItem {
    private final String title;

    public MediaItem(String title) {
        this.title = title;
    }

    public String getInfo() {
        return title;
    }

    public void play() {
        System.out.println("Playing media item...");
    }
}

class Book extends MediaItem {
    private final String author;

    public Book(String title, String author) {
        super(title);
        this.author = author;
    }

    @Override
    public void play() {
        System.out.println("Reading the book: " + getInfo() + " by " + author + ".");
    }

    public String getAuthor() {
        return author;
    }
}

class Movie extends MediaItem {
    private int durationMinutes;

    public Movie(String title, int durationMinutes) {
        super(title);
        this.durationMinutes = durationMinutes;
    }

    @Override
    public void play() {
        System.out.println("Watching the movie: " + getInfo() + " (" + durationMinutes + " minutes).");
    }

    public int getDuration() {
        return durationMinutes;
    }
}

class Library {
    private MediaItem[] featuredShelf;
    private ArrayList<MediaItem> catalog;

    public Library() {
        featuredShelf = new MediaItem[5];
        catalog = new ArrayList<>();
    }

    public void addToCatalog(MediaItem item) {
        catalog.add(item);
    }

    public void setFeaturedItem(int index, MediaItem item) {
        featuredShelf[index] = item;
    }

    public void display() {
        System.out.println("Books:");
        for (MediaItem item : catalog) {
            if (item instanceof Book) {
                System.out.println(item.getInfo());
            }
        }

        System.out.println("Movies:");
        for (MediaItem item : catalog) {
            if (item instanceof Movie) {
                System.out.println(item.getInfo());
            }
        }
    }

    public void saveToFile() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = sc.nextLine();

        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            for (MediaItem item : catalog) {
                if (item instanceof Book) {
                    Book b = (Book) item;
                    // Save BOTH title and author
                    out.println("BOOK|" + b.getInfo() + "|" + b.getAuthor());
                } else if (item instanceof Movie) {
                    Movie m = (Movie) item;
                    // Save BOTH title and duration
                    out.println("MOVIE|" + m.getInfo() + "|" + m.getDuration());
                } else {
                    out.println("MEDIA|" + item.getInfo());
                }
            }
        } catch (Exception e) {
            System.out.println("Error writing file.");
        }
    }

    public void uploadFromFile() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = sc.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            catalog.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;

                String type = parts[0];

                if (type.equals("BOOK") && parts.length >= 3) {
                    String title = parts[1];
                    String author = parts[2];
                    catalog.add(new Book(title, author));
                } else if (type.equals("MOVIE") && parts.length >= 3) {
                    String title = parts[1];
                    int duration = Integer.parseInt(parts[2]);
                    catalog.add(new Movie(title, duration));
                } else if (parts.length >= 2) {
                    catalog.add(new MediaItem(parts[1]));
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading file.");
        }
    }

    public boolean searchRecursive(String title) {
        return searchRecursive(title, 0);
    }

    private boolean searchRecursive(String title, int index) {
        if (index >= catalog.size()) return false;
        if (catalog.get(index).getInfo().equalsIgnoreCase(title)) return true;
        return searchRecursive(title, index + 1);
    }

    public MediaItem[] getFeaturedShelf() {
        return featuredShelf;
    }

    public ArrayList<MediaItem> getCatalog() {
        return catalog;
    }
}