package org.agnitas.preview;

import java.util.Hashtable;

public interface Preview {


    /* Special IDs for identifing parts of message */
    /** The ID for the complete header */
    public static final String ID_HEAD = "__head__";
    /** The ID for a hashtable for individual header lines */
    public static final String ID_HDETAIL = "__head_detail__";
    /** The ID for the text part */
    public static final String ID_TEXT = "__text__";
    /** The ID for the HTML part */
    public static final String ID_HTML = "__html__";
    /** The ID for the MHTML part */
    public static final String ID_MHTML = "__mhtml__";
    /** The ID for an error, if one had occured */
    public static final String ID_ERROR = "__error__";

    /** done
     * CLeanup code
     */
    public abstract void done();

    public abstract int getMaxAge();

    public abstract void setMaxAge(int nMaxAge);

    public abstract int getMaxEntries();

    public abstract void setMaxEntries(int nMaxEntries);

    /** mkMailgun
     * Creates a new instance for a mailgun
     * @return the new instance
     */
    public abstract Object mkMailgun() throws Exception;
    public abstract Page mkPage ();

    /** makePreview
     * The main entrance for this class, a preview for all
     * parts of the mail is generated into a hashtable for
     * the given mailing and customer. If cachable is set
     * to true, the result is cached for speed up future
     * access.
     * @param mailingID the mailing-id to create the preview for
     * @param customerID the customer-id to create the preview for
     * @param selector optional selector for selecting different version of cached page
     * @param anon if we should anonymize the result
     * @param convertEntities replace non ascii characters by ther HTML entity representation
     * @param legacyUIDs if set we should stick to legacy UIDs
     * @param createAll if set create all displayable parts of the mailing
     * @param cachable if the result should be cached
     * @return the preview
     */
    public abstract Page makePreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean convertEntities, boolean legacyUIDs, boolean createAll, boolean cachable);
    public abstract Page makePreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean convertEntities, boolean legacyUIDs, boolean cachable);
    public abstract Page makePreview (long mailingID, long customerID, String selector, String text, boolean anon, boolean cachable);
    public abstract Page makePreview (long mailingID, long customerID, String selector, boolean anon, boolean cachable);
    public abstract Page makePreview (long mailingID, long customerID, boolean cachable);
    /* Wrapper for heatmap generation
     * @param mailingID the mailing to generate the heatmap for
     * @param customerID the customerID to generate the heatmap for
     * @return the preview
     */
    public abstract String makePreviewForHeatmap (long mailingID, long customerID);

    @Deprecated
    public abstract Hashtable <String, Object> createPreview (long mailingID,
            long customerID, String selector, String text, boolean anon,
            boolean convertEntities, boolean legacyUIDs, boolean createAll,
            boolean cachable);
    @Deprecated
    public abstract Hashtable <String, Object> createPreview (long mailingID,
            long customerID, String selector, String text, boolean anon,
            boolean convertEntities, boolean legacyUIDs, boolean cachable);
    @Deprecated
    public abstract Hashtable<String, Object> createPreview(long mailingID,
            long customerID, String selector, String text, boolean anon,
            boolean cachable);
    @Deprecated
    public abstract Hashtable<String, Object> createPreview(long mailingID,
            long customerID, String selector, boolean anon,
            boolean cachable);
    @Deprecated
    public abstract Hashtable<String, Object> createPreview(long mailingID,
            long customerID,
            boolean cachable);

    /**
     * Get header-, text- or HTML-part from hashtable created by
     * createPreview as byte stream
     */
    @Deprecated
    public abstract byte[] getHeaderPart(Hashtable<String, Object> output,
            String charset, boolean escape);

    @Deprecated
    public abstract byte[] getHeaderPart(Hashtable<String, Object> output,
            String charset);

    @Deprecated
    public abstract byte[] getTextPart(Hashtable<String, Object> output,
            String charset, boolean escape);

    @Deprecated
    public abstract byte[] getTextPart(Hashtable<String, Object> output,
            String charset);

    @Deprecated
    public abstract byte[] getHTMLPart(Hashtable<String, Object> output,
            String charset, boolean escape);

    @Deprecated
    public abstract byte[] getHTMLPart(Hashtable<String, Object> output,
            String charset);

    /**
     * Get header-, text- or HTML-part as strings
     */
    @Deprecated
    public abstract String getHeader(Hashtable<String, Object> output,
            boolean escape);

    @Deprecated
    public abstract String getHeader(Hashtable<String, Object> output);

    @Deprecated
    public abstract String getText(Hashtable<String, Object> output,
            boolean escape);

    @Deprecated
    public abstract String getText(Hashtable<String, Object> output);

    @Deprecated
    public abstract String getHTML(Hashtable<String, Object> output,
            boolean escape);

    @Deprecated
    public abstract String getHTML(Hashtable<String, Object> output);

    /**
     * Get individual lines from the header
     */
    @Deprecated
    public abstract String[] getHeaderField(Hashtable<String, Object> output,
            String field);

    @Deprecated
    public abstract String getPartOfHeader(Hashtable<String, Object> output,
            boolean escape, String headerKeyword);

    /**
     * Get attachment names and content
     */
    @Deprecated
    public abstract String[] getAttachmentNames (Hashtable <String, Object> output);

    @Deprecated
    public abstract byte[] getAttachment (Hashtable <String, Object> output, String name);

    // well, we could create a global Hashmap containing all the values for this preview
    // but the part-Method is called not very often, so its more efficient to parse
    // the header if we need it.
    // As parameter give the "Keyword" you will get then the appropriate return String.
    // Possible Values for the Header are:
    // "Return-Path", "Received", "Message-ID", "Date", "From", "To", "Subject", "X-Mailer", "MIME-Version"
    // warning! We do a "startswith" comparison, that means, if you give "Re" as parameter, you will
    // get either "Return-Path" or "Received", depending on what comes at last.
    @Deprecated
    public abstract String getPartOfHeader(Hashtable<String, Object> output,
            String charset, boolean forHTML, String headerKeyword);

}
