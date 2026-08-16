package cream.cli.view;

import cream.cli.Client;
import cream.cli.control.handler.*;
import cream.cli.control.focus.FocusManager;
import cream.cli.model.WorkspaceModel;
import cream.cli.model.WorkspaceModel.WorkspaceListener;
import cream.cli.view.editor.Editor;
import cream.cli.view.editor.EditorFileManager;
import cream.cli.view.footer.Footer;
import cream.cli.view.header.Header;
import cream.cli.view.files.Files;
import cream.cli.view.omnibox.Omnibox;
import cream.cli.view.result.ResultError;
import cream.cli.view.result.ResultProgress;
import cream.cli.view.result.ResultSearch;
import cream.cli.view.theme.ThemeService;
import cream.cli.view.ui.Bubble;
import cream.cli.view.ui.EditorDialog;
import cream.cli.view.viewer.ImageViewer;
import fasttui.component.Container;

import java.io.File;
import java.nio.file.Path;

public class ViewManager {

    public final FocusManager focusManager;
    public final WorkspaceModel workspaceModel;
    public final Container container;
    public final Files files;
    public final Header header;
    public final Editor editor;
    public final EditorDialog editorDialog;
    public final ImageViewer imageViewer;
    public final Omnibox omnibox;
    public final Footer footer;
    public final ResultSearch resultSearch;
    public final ResultProgress resultProgress;
    public final ResultError resultError;
    public final Bubble bubble;
    public final Bubble userBubble;
    public final FilesController filesController;
    public final EditorController editorController;
    public final ImageViewController imageViewController;
    public final OmniboxTextController omniboxTextController;
    public final FooterButtonController footerModeController;
    public final FooterButtonController footerServiceController;
    public final FooterButtonController footerModelController;
    public final FooterButtonController footerTasksController;

    public ViewManager(int cols, int rows, Runnable repaintCallback, Client client) {

        this.workspaceModel = new WorkspaceModel();
        this.focusManager = new FocusManager();

        this.files = new Files(0, 1, cols, rows - 5, repaintCallback);
        this.files.getFilesList().getFilesState().focusManager = this.focusManager;
        this.files.getFilesList().getFilesState().workspaceModel = this.workspaceModel;
        this.header = new Header(0, 0, cols, 1, this.files.getFilesList().getFilesState());
        this.editor = new Editor(0, 1, cols, rows - 5, repaintCallback);
        this.editorDialog = new EditorDialog(cols, rows);
        this.imageViewer = new ImageViewer(0, 1, cols - 1, rows - 4);
        this.omnibox = new Omnibox(1, rows - 4, cols - 2, 3);
        this.resultSearch = new ResultSearch(1, rows - 8, cols - 2, 5, rows);
        this.resultProgress = new ResultProgress(cols, rows);
        this.resultError = new ResultError(1, rows, cols - 2, repaintCallback);
        this.bubble = new Bubble(1, rows - 9, cols - 2);
        this.userBubble = new Bubble(1, rows - 14, cols - 2);
        this.footer = new Footer(cols, rows);

        this.filesController = new FilesController(this.files.getFilesList(), client);
        this.editorController = new EditorController(client, this.editor);
        this.imageViewController = new ImageViewController(this.imageViewer, client);
        this.omniboxTextController = new OmniboxTextController(client, this);
        this.footerModeController = new FooterButtonController(this.footer.mode, this.footer.popupMode, this.focusManager);
        this.footerServiceController = new FooterButtonController(this.footer.service, this.footer.popupService, this.focusManager);
        this.footerModelController = new FooterButtonController(this.footer.model, this.footer.popupModel, this.focusManager);
        this.footerTasksController = new FooterButtonController(this.footer.tasks, this.footer.popupTasks, this.focusManager);

        this.focusManager.registerTarget(this.filesController);
        this.focusManager.registerTarget(this.editorController);
        this.focusManager.registerTarget(this.omniboxTextController);
        this.focusManager.registerTarget(this.footerModeController);
        this.focusManager.registerTarget(this.footerServiceController);
        this.focusManager.registerTarget(this.footerModelController);
        this.focusManager.registerTarget(this.footerTasksController);
        this.focusManager.setCurrentComponent(this.filesController);

        this.workspaceModel.addListener(new MyWorkspaceListener(repaintCallback));

        this.files.getFilesList().setFileOpenListener(this.workspaceModel::setActiveFile);

        this.header.setNavigationCallback(path -> {
            Path targetPath = this.workspaceModel.resolvePath(path);
            this.workspaceModel.setCurrentDirectory(targetPath);
            this.workspaceModel.setActiveFile(null);
        });

        this.omnibox.text.addStateChangeListener(source -> {
            this.onInputFocusChanged(source.isFocused());
        });

        this.editor.setVisible(false);
        this.imageViewer.setVisible(false);
        this.resultSearch.setVisible(false);
        this.resultProgress.setVisible(false);

        this.container = new Container(0, 0, cols, rows);
        this.container.setBackgroundColor(ThemeService.get().getBackground());
        this.container.add(this.header);
        this.container.add(this.files);
        this.container.add(this.editor);
        this.container.add(this.editor.getEditorAutocompleteManager().getAutocompletePopup());
        this.container.add(this.editorDialog);
        this.container.add(this.imageViewer);
        this.container.add(this.omnibox);
        this.container.add(this.footer);
        this.container.add(this.resultSearch);
        this.container.add(this.resultProgress);
        this.container.add(this.resultError);
        this.container.add(this.userBubble);
        this.container.add(this.bubble);
    }

    public void showExplorer() {
        final EditorFileManager editorFileManager = this.editor.getEditorFileManager();
        final File currentFile = editorFileManager.getCurrentFile();
        final File currentDirectory = currentFile.getParentFile();
        this.files.getFilesList().navigateTo(currentDirectory);
        this.header.setOverridePath(null);
//        this.header.setVisible(true);
        this.files.getFilesHeader().setVisible(true);
        this.files.getFilesList().setVisible(true);
        this.files.getFilesFooter().setVisible(true);
        if (this.focusManager.getCurrentComponent() != this.omniboxTextController) {
            this.focusManager.setCurrentComponent(this.filesController);
        }
        this.editor.setVisible(false);
        this.imageViewer.setVisible(false);
    }

    public void showEditor() {
        this.files.getFilesHeader().setVisible(false);
        this.files.getFilesList().setVisible(false);
        this.files.getFilesFooter().setVisible(false);
        this.imageViewer.setVisible(false);
//        this.header.setVisible(true);
        this.editor.setVisible(true);
        this.focusManager.setCurrentComponent(this.editorController);
    }

    public void showImageViewer(File file) {
        if (this.files != null) {
            this.files.getFilesHeader().setVisible(false);
            this.files.getFilesList().setVisible(false);
            this.files.getFilesFooter().setVisible(false);
        }
        if (this.editor != null) {
            this.editor.setVisible(false);
        }
            this.header.setOverridePath(file != null ? file.getAbsolutePath() : null);
//            this.header.setVisible(true);
        if (this.imageViewer != null && file != null) {
            this.imageViewer.setVisible(true);
            this.imageViewer.loadImage(file);
            this.focusManager.setCurrentComponent(this.imageViewController);
        }
    }

    public void handleTerminalResize(int cols, int rows) {
        this.container.setWidth(cols);
        this.container.setHeight(rows);

        if (this.omnibox != null) {
            this.omnibox.setY(rows - 4);
        }
        if (this.resultSearch != null) {
            this.resultSearch.setY(rows - 8);
        }
        if (this.editorDialog != null) {
            this.editorDialog.center(cols, rows);
        }
    }

    private void onInputFocusChanged(final boolean focused) {
        // Focus listener implementation hook
    }

    public Editor getEditor() {
        return this.editor;
    }

    public Files getNavigator() {
        return this.files;
    }

    public Omnibox getOmnibox() {
        return this.omnibox;
    }

    public Footer getFooter() {
        return this.footer;
    }

    public WorkspaceModel getWorkspaceModel() {
        return this.workspaceModel;
    }

    public Container getContainer() {
        return this.container;
    }

    public EditorDialog getEditorDialog() {
        return this.editorDialog;
    }

    public ResultSearch getResultSearch() {
        return resultSearch;
    }

    public ResultProgress getResultProgress() {
        return resultProgress;
    }

    public FocusManager getFocusManager() {
        return this.focusManager;
    }

    private class MyWorkspaceListener implements WorkspaceListener {
        private final Runnable repaintCallback;

        public MyWorkspaceListener(Runnable repaintCallback) {
            this.repaintCallback = repaintCallback;
        }

        @Override
        public void onDirectoryChanged(Path newDirectory) {
            if (newDirectory != null) {
                files.getFilesList().setCurrentDirectory(newDirectory.toString());
                files.getFilesList().refreshFiles();
                header.setOverridePath(null);
                if (repaintCallback != null) repaintCallback.run();
            }
        }

        @Override
        public void onActiveFileChanged(File newFile) {
            if (newFile != null) {
                header.setOverridePath(newFile.getAbsolutePath());
                String name = newFile.getName().toLowerCase();
                if (name.endsWith(".png") ||
                        name.endsWith(".jpg") ||
                        name.endsWith(".jpeg") ||
                        name.endsWith(".gif") ||
                        name.endsWith(".bmp") ||
                        name.endsWith(".svg")) {
                    showImageViewer(newFile);
                } else {
                    editor.getEditorFileManager().loadFile(newFile);
                    showEditor();
                }
            } else {
                header.setOverridePath(null);
                showExplorer();
            }
            if (repaintCallback != null) repaintCallback.run();
        }
    }
}
