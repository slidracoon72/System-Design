// Main class representing the File System
class FileSystem {

	Directory rootDirectory;

	public FileSystem() {
		this.rootDirectory = new Directory("/", null);
	}

	public File createFile(String name, Directory parent, int size, Permissions permissions) {
		File file = new File(name, parent, size, permissions);
		parent.addContent(file);
		return file;
	}

	public Directory createDirectory(String name, Directory parent, Permissions permissions) {
		Directory directory = new Directory(name, parent, permissions);
		parent.addContent(directory);
		return directory;
	}
}

// Abstract class for common properties
abstract class FileSystemEntity {

	String name;
	Date createdAt;
	Date updatedAt;
	Directory parent;
	Permissions permissions;

	public FileSystemEntity(String name, Directory parent, Permissions permissions) {
		this.name = name;
		this.parent = parent;
		this.permissions = permissions;
		this.createdAt = new Date();
		this.updatedAt = new Date();
	}

	public abstract int getSize();

	public boolean canRead() {
		return permissions.read;
	}

	public boolean canWrite() {
		return permissions.write;
	}

	public boolean canExecute() {
		return permissions.execute;
	}
}

// Represents a File
class File extends FileSystemEntity {

	int size; // size in bytes

	public File(String name, Directory parent, int size, Permissions permissions) {
		super(name, parent, permissions);
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
}

// Represents a Directory
class Directory extends FileSystemEntity {

	List<FileSystemEntity> contents;

	public Directory(String name, Directory parent, Permissions permissions) {
		super(name, parent, permissions);
		contents = new ArrayList<>();
	}

	public void addContent(FileSystemEntity entity) {
		contents.add(entity);
	}

	public void removeContent(FileSystemEntity entity) {
		contents.remove(entity);
	}

	@Override
	public int getSize() {
		int totalSize = 0;
		for (FileSystemEntity entity : contents) {
			totalSize += entity.getSize();
		}
		return totalSize;
	}
}

// User class who can interact with FileSystem
class User {

	String username;
	int userId;
}

// Permissions related to Files/Directories
class Permissions {

	boolean read;
	boolean write;
	boolean execute;

	public Permissions(boolean read, boolean write, boolean execute) {
		this.read = read;
		this.write = write;
		this.execute = execute;
	}
}

// File Manager class to perform operations like move, delete, search
class FileManager {

	public void move(FileSystemEntity entity, Directory destination) {
		if (entity.parent != null) {
			entity.parent.removeContent(entity);
		}
		destination.addContent(entity);
		entity.parent = destination;
	}

	public void delete(FileSystemEntity entity) {
		if (entity.parent != null) {
			entity.parent.removeContent(entity);
		}
	}

	public List<FileSystemEntity> listContents(Directory directory) {
		return directory.contents;
	}

	// Search for a file or directory by name (DFS search)
	public FileSystemEntity search(Directory directory, String name) {
		if (directory.name.equals(name)) {
			return directory;
		}

		for (FileSystemEntity entity : directory.contents) {
			if (entity.name.equals(name)) {
				return entity;
			}
			if (entity instanceof Directory) {
				FileSystemEntity found = search((Directory) entity, name);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	// Find all files matching a name pattern
	public List<File> findFilesByName(Directory directory, String namePattern) {
		List<File> matchedFiles = new ArrayList<>();
		dfsFindFiles(directory, namePattern, matchedFiles);
		return matchedFiles;
	}

	private void dfsFindFiles(Directory directory, String namePattern, List<File> matchedFiles) {
		for (FileSystemEntity entity : directory.contents) {
			if (entity instanceof File && entity.name.contains(namePattern)) {
				matchedFiles.add((File) entity);
			}
			if (entity instanceof Directory) {
				dfsFindFiles((Directory) entity, namePattern, matchedFiles);
			}
		}
	}
}

// Enum to define different types of files (optional extension)
public enum FileType {
	REGULAR, EXECUTABLE, SYMBOLIC_LINK;
}
