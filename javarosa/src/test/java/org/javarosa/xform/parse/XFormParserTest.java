package org.javarosa.xform.parse;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.javarosa.xform.parse.ValidationMessage.Type;
import static org.javarosa.xform.parse.ValidationMessageBuilder.validationMessage;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class XFormParserTest {
    @Test
    public void validate_shouldReturnErrorIfDocumentIsNotAValidXML() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/emptyDocument.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Document has no root element!").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfRootElementIsInvalid() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidRootElement.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("XForm Parse: Unrecognized element [xforms]. Ignoring and processing children...\n" +
                "    Problem found at nodeset: /xforms\n" +
                "    With element <xforms>\n").withType(Type.ERROR).instance()));
    }

    private FileReader getFile(String file) throws FileNotFoundException {
        return new FileReader(this.getClass().getClassLoader().getResource(file).getFile());
    }

    @Test
    public void validate_shouldReturnWarningIfModelHasInvalidAttribute() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidModelAttribute.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Warning: 1 Unrecognized attributes found in Element [model] and will be ignored: [id] Location:\n" +
                "\n" +
                "    Problem found at nodeset: /html/model\n" +
                "    With element <model id=\"openmrs_model\">\n").withType(Type.WARNING).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfModelHasInvalidChild() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidModelChild.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Unrecognized top-level tag [invalidChildTag] found within <model>\n" +
                "    Problem found at nodeset: /html/head/model/invalidChildTag\n" +
                "    With element <invalidChildTag>\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfThereAreMultipleModels() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/multipleModels.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Multiple models not supported. Ignoring subsequent models.\n" +
                "    Problem found at nodeset: /html/head/model\n" +
                "    With element <model>\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfModelTextIsInvalid() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidModelText.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Unrecognized text content found within <model>: \"invalid text content\"").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfITextHasNoTranslations() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidITextNoTranslations.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("no <translation>s defined\n" +
                "    Problem found at nodeset: /html/head/model/itext\n" +
                "    With element <itext>\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnWarningIfITextInvalidAttribute() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidITextAttribute.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Warning: 1 Unrecognized attributes found in Element [itext] and will be ignored: [invalid] Location:\n" +
                "\n" +
                "    Problem found at nodeset: /html/head/model/itext\n" +
                "    With element <itext invalid=\"attribute\">\n").withType(Type.WARNING).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfITextHasTranslationTagWithoutLanguageAttribute() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/translationTagWithoutLanguageAttribute.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("no language specified for <translation>\n" +
                "    Problem found at nodeset: /html/head/model/itext/translation\n" +
                "    With element <translation>\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfITextHasDuplicateTranslationTag() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/duplicateTranslationTag.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("duplicate <translation> for language 'english'\n" +
                "    Problem found at nodeset: /html/head/model/itext/translation\n" +
                "    With element <translation lang=\"english\">\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfITextHasDuplicateLanguageDefault() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/duplicateLanguageDefault.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("more than one <translation> set as default\n" +
                "    Problem found at nodeset: /html/head/model/itext/translation\n" +
                "    With element <translation lang=\"spanish\" default=\"true\">\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfTextHasNoId() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/textWithoutIdAttribute.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("no id defined for <text>\n" +
                "    Problem found at nodeset: /html/head/model/itext/translation[@lang=english][@default=true]/text\n" +
                "    With element <text>\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfTextHasInvalidChild() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/textInvalidChild.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Unrecognized element [invalid] in " +
                "Itext->translation->text").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnErrorIfTextDefinitionIsDuplicate() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/duplicateTextDefinition.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("duplicate definition for text ID \"q1\" and form \"null\". Can only have one definition for each text form.\n" +
                "    Problem found at nodeset: /html/head/model/itext/translation[@lang=english][@default=true]/text\n" +
                "    With element <text id=\"q1\">\n").withType(Type.ERROR).instance()));
    }

    @Test
    public void validate_shouldReturnWarningForTextChildInvalidAttribute() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidTextChildAttribute.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Warning: 1 Unrecognized attributes found in Element [value] and will be ignored: [invalid] Location:\n" +
                "\n" +
                "    Problem found at nodeset: /html/head/model/itext/translation[@lang=english][@default=true]/text[@id=q1]/value\n" +
                "    With element <value invalid=\"invalid\">\n").withType(Type.WARNING).instance()));
    }

    @Test
    public void validate_shouldReturnWarningForTextInvalidAttribute() throws Exception {
        XFormParser parser = new XFormParser(getFile("javarosa/invalidTextAttribute.xml"));
        ValidationMessages messages = parser.validate();
        assertThat(messages.list, hasItem(validationMessage().withMessage("Warning: 1 Unrecognized attributes found in Element [text] and will be ignored: [invalid] Location:\n" +
                "\n" +
                "    Problem found at nodeset: /html/head/model/itext/translation[@lang=english][@default=true]/text\n" +
                "    With element <text id=\"q1\" invalid=\"invalid\">\n").withType(Type.WARNING).instance()));
    }

}
