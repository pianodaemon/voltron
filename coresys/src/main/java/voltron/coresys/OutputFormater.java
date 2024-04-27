package voltron.coresys;

public interface OutputFormater<W> {

    public W render() throws VoltronException;

    public void saveOnStorage(String filePath, W w) throws VoltronException;

    default public void renderFeaturingSave(String xmlFilePath) throws VoltronException {
        saveOnStorage(xmlFilePath, render());
    }
}
