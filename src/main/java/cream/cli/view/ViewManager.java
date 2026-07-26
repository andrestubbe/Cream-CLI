package cream.cli.view;

import cream.cli.Client;
import cream.cli.Theme;
import cream.cli.control.EditorController;
import cream.cli.control.FilesController;
import cream.cli.control.FocusManager;
import cream.cli.control.ImageViewController;
import cream.cli.control.OmniboxButtonController;
import cream.cli.control.OmniboxTextController;
import cream.cli.model.FileCategory;
import cream.cli.model.WorkspaceModel;
import cream.cli.view.editor.Editor;
import cream.cli.view.editor.ImageViewer;
import cream.cli.view.files.Navigator;
import cream.cli.view.header.PathHeader;
import cream.cli.view.omnibox.Omnibox;
import cream.cli.view.result.Heatmap;
import cream.cli.view.result.ResultProgress;
import cream.cli.view.result.ResultSearch;
import cream.cli.view.ui.Dialog;
import fasttui.component.Container;
import java.io.File;

public class ViewManager {

    public final Container container;
    public final Navigator navigator;
    public final PathHeader pathHeader;
    public final Editor editor;
    public final ImageViewer imageViewer;
    public final Dialog dialog;
    public final ResultSearch resultSearch;
    public final ResultProgress resultProgress;
    public final Heatmap heatmap;
    public final Omnibox omnibox;

    public final FocusManager focusManager = new FocusManager();
    public final FilesController filesController;
    public final EditorController editorController;
    public final ImageViewController imageViewController;
    public final OmniboxTextController omniboxTextController;
    public final OmniboxButtonController omniboxModeController;
    public final OmniboxButtonController omniboxServiceController;
    public final OmniboxButtonController omniboxModelController;
    public final WorkspaceModel workspaceModel;

    public ViewManager(int cols, int rows, Runnable repaintCallback, Client client) {
        this.container = new Container(0, 0, cols, rows);
        this.container.setBackgroundColor(Theme.BACKGROUND);

        this.workspaceModel = new WorkspaceModel();

        this.navigator = new Navigator(cols, rows, repaintCallback);
        this.navigator.files.getState().focusManager = this.focusManager;
        this.navigator.files.getState().workspaceModel = this.workspaceModel;
        this.pathHeader = new PathHeader(0, 0, cols, this.navigator.files.getState());
        this.filesController = new FilesController(this.navigator.files, client);

        this.editor = new Editor(cols, rows - 1);
        this.editor.setX(0);
        this.editor.setY(1);
        this.editor.setVisible(false);
        this.editor.setRepaintTrigger(repaintCallback);
        this.editorController = new EditorController(this.editor, client);

        this.imageViewer = new ImageViewer(0, 1, cols - 1, rows - 4);
        this.imageViewer.setVisible(false);
        this.imageViewController = new ImageViewController(this.imageViewer, client);

        this.omnibox = new Omnibox(cols, rows);

        this.omniboxTextController = new OmniboxTextController(this.omnibox, this.workspaceModel, client);
        this.omniboxModeController = new OmniboxButtonController(this.omnibox.mode, this.omnibox.popupMode, this.focusManager);
        this.omniboxServiceController = new OmniboxButtonController(this.omnibox.service, this.omnibox.popupService, this.focusManager);
        this.omniboxModelController = new OmniboxButtonController(this.omnibox.model, this.omnibox.popupModel, this.focusManager);

        this.focusManager.registerTarget(this.filesController);
        this.focusManager.registerTarget(this.editorController);
        this.focusManager.registerTarget(this.imageViewController);
        this.focusManager.registerTarget(this.omniboxTextController);
        this.focusManager.registerTarget(this.omniboxModeController);
        this.focusManager.registerTarget(this.omniboxServiceController);
        this.focusManager.registerTarget(this.omniboxModelController);
        this.focusManager.setCurrentComponent(this.filesController);

        this.dialog = new Dialog(cols, rows);

        // Milestone 85: Console-to-Visual State Synchronizer via WorkspaceModel
        this.workspaceModel.addListener(new WorkspaceModel.WorkspaceListener() {
            @Override
            public void onDirectoryChanged(java.nio.file.Path newDirectory) {
                if (newDirectory != null) {
                    navigator.files.setCurrentDirectory(newDirectory.toString());
                    navigator.files.refreshFiles();
                    pathHeader.setOverridePath(null);
                    if (repaintCallback != null) repaintCallback.run();
                }
            }

            @Override
            public void onActiveFileChanged(File newFile) {
                if (newFile != null) {
                    FileCategory cat = FileCategory.fromPath(newFile.getName());
                    if (cat == FileCategory.IMAGE) {
                        showImageViewer(newFile);
                    } else if (cat.isOpenableInEditor()) {
                        editor.fileManager.loadFile(newFile);
                        pathHeader.setOverridePath(newFile.getAbsolutePath());
                        showEditor();
                    }
                } else {
                    showExplorer();
                }
                if (repaintCallback != null) repaintCallback.run();
            }
        });

        this.navigator.files.setFileOpenListener(file -> {
            this.workspaceModel.setActiveFile(file);
        });

        this.pathHeader.setNavigationCallback(path -> {
            java.nio.file.Path targetPath = this.workspaceModel.resolvePath(path);
            this.workspaceModel.setCurrentDirectory(targetPath);
            this.workspaceModel.setActiveFile(null);
        });

        this.omnibox.text.addStateChangeListener(source -> {
            this.onInputFocusChanged(source.isFocused());
        });

        this.resultSearch = new ResultSearch(cols, rows);
        this.resultProgress = new ResultProgress(cols, rows);
        this.heatmap = new Heatmap(this.omnibox, cols, rows);

        this.container.add(this.pathHeader);
        this.container.add(this.navigator);
        this.container.add(this.editor);
        this.container.add(this.imageViewer);
        this.container.add(this.omnibox);
        this.container.add(this.resultSearch);
        this.container.add(this.resultProgress);
//      this.container.add(this.heatmap);
//      this.container.add(this.omnibox.popupMode);
//      this.container.add(this.omnibox.popupService);
//      this.container.add(this.omnibox.popupModel);
        this.container.add(this.editor.autocompleteMgr.getAutocompletePopup());
        this.container.add(this.dialog);
    }

    public void showExplorer() {
        if (this.pathHeader != null) {
            this.pathHeader.setOverridePath(null);
            this.pathHeader.setVisible(true);
        }
        if (this.navigator != null) {
            this.navigator.columnHeader.setVisible(true);
            this.navigator.files.setVisible(true);
            this.navigator.footer.setVisible(true);
            if (this.focusManager.getCurrentComponent() != this.omniboxTextController) {
                this.focusManager.setCurrentComponent(this.filesController);
            }
        }
        if (this.editor != null) {
            this.editor.setVisible(false);
        }
        if (this.imageViewer != null) {
            this.imageViewer.setVisible(false);
        }
    }

    public void showEditor() {
        if (this.navigator != null) {
            this.navigator.columnHeader.setVisible(false);
            this.navigator.files.setVisible(false);
            this.navigator.footer.setVisible(false);
        }
        if (this.imageViewer != null) {
            this.imageViewer.setVisible(false);
        }
        if (this.pathHeader != null) {
            this.pathHeader.setVisible(true);
        }
        if (this.editor != null) {
            this.editor.setVisible(true);
            this.focusManager.setCurrentComponent(this.editorController);
        }
    }

    public void showImageViewer(File file) {
        if (this.navigator != null) {
            this.navigator.columnHeader.setVisible(false);
            this.navigator.files.setVisible(false);
            this.navigator.footer.setVisible(false);
        }
        if (this.editor != null) {
            this.editor.setVisible(false);
        }
        if (this.pathHeader != null) {
            this.pathHeader.setOverridePath(file != null ? file.getAbsolutePath() : null);
            this.pathHeader.setVisible(true);
        }
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
        if (this.dialog != null) {
            this.dialog.center(cols, rows);
        }
    }

    public void prepareRepaint(int rows) {
        if (this.omnibox != null) {
            this.omnibox.adjustHeight(rows);
        }
    }

    private void onInputFocusChanged(boolean focused) {
        // Focus listener implementation hook
    }
}
