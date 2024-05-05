package voltron.coresys.tampering;

import voltron.coresys.VoltronException;

interface OutputFormater<W> {

    public W render() throws VoltronException;

    public void saveOnStorage(String filePath, W w) throws VoltronException;

    default public void renderFeaturingSave(String xmlFilePath) throws VoltronException {
        saveOnStorage(xmlFilePath, render());
    }
}
