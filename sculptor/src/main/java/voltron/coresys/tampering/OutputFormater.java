package voltron.coresys.tampering;

import voltron.coresys.SculptorException;

interface OutputFormater<W> {

    public W render() throws SculptorException;

    public void saveOnStorage(String filePath, W w) throws SculptorException;

    default public void renderFeaturingSave(String xmlFilePath) throws SculptorException {
        saveOnStorage(xmlFilePath, render());
    }
}
