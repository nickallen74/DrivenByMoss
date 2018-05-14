// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.ObserverManagement;
import de.mossgrabers.framework.daw.resource.ChannelType;


/**
 * The interface to a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IChannel extends ObserverManagement
{
    /**
     * Get the index of the channel in the current bank page.
     *
     * @return The index of the channel in the current bank page
     */
    int getIndex ();


    /**
     * True if the channel is selected.
     *
     * @return True if the channel is selected.
     */
    boolean isSelected ();


    /**
     * Set the selected state of the channel.
     *
     * @param isSelected True if the channel is selected
     */
    void setSelected (boolean isSelected);


    /**
     * Returns true if the channel exits.
     *
     * @return True if the channel exits.
     */
    boolean doesExist ();


    /**
     * Returns true if the channel is activated.
     *
     * @return True if the channel is activated
     */
    boolean isActivated ();


    /**
     * Get the name of the channel.
     *
     * @return The name of the channel
     */
    String getName ();


    /**
     * Get the name of the channel.
     *
     * @param limit Limit the text to this length
     * @return The name
     */
    String getName (int limit);


    /**
     * Get the type of the track.
     *
     * @return The type
     */
    ChannelType getType ();


    /**
     * Get the volume as a formatted text.
     *
     * @return The volume text
     */
    String getVolumeStr ();


    /**
     * Get the volume as a formatted text.
     *
     * @param limit Limit the text to this length
     * @return The volume text
     */
    String getVolumeStr (int limit);


    /**
     * Get the volume.
     *
     * @return The volume
     */
    int getVolume ();


    /**
     * Change the volume.
     *
     * @param control The control value
     */
    void changeVolume (int control);


    /**
     * Set the volume.
     *
     * @param value The new value
     */
    void setVolume (double value);


    /**
     * Reset the volume to its default value.
     */
    void resetVolume ();


    /**
     * Signal that the volume fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchVolume (boolean isBeingTouched);


    /**
     * Signal that the volume is edited.
     *
     * @param indicate True if edited
     */
    void setVolumeIndication (boolean indicate);


    /**
     * Get the modulated volume.
     *
     * @return The modulated volume
     */
    int getModulatedVolume ();


    /**
     * Get the panorama as a formatted text
     *
     * @return The panorama text
     */
    String getPanStr ();


    /**
     * Get the panorama as a formatted text
     *
     * @param limit Limit the text to this length
     * @return The panorama text
     */
    String getPanStr (int limit);


    /**
     * Get the panorama.
     *
     * @return The panorama
     */
    int getPan ();


    /**
     * Change the panorama.
     *
     * @param control The control value
     */
    void changePan (int control);


    /**
     * Set the panorama.
     *
     * @param value The new value
     */
    void setPan (double value);


    /**
     * Reset the panorama to its default value.
     */
    void resetPan ();


    /**
     * Signal that the panorama fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchPan (boolean isBeingTouched);


    /**
     * Signal that the panorama is edited.
     *
     * @param indicate True if edited
     */
    void setPanIndication (boolean indicate);


    /**
     * Get the modulated panorama.
     *
     * @return The modulated panorama
     */
    int getModulatedPan ();


    /**
     * Sets the activated state of the track.
     *
     * @param value True to activate
     */
    void setIsActivated (boolean value);


    /**
     * Toggle the activated state of the track.
     */
    void toggleIsActivated ();


    /**
     * Get the color of the channel.
     *
     * @return The color in RGB
     */
    double [] getColor ();


    /**
     * Set the color of the track as a RGB value.
     *
     * @param red The red part of the color
     * @param green The green part of the color
     * @param blue The blue part of the color
     */
    void setColor (double red, double green, double blue);


    /**
     * True if muted.
     *
     * @return True if muted.
     */
    boolean isMute ();


    /**
     * Turn on/off mute.
     *
     * @param value True to turn on mute, otherwise off
     */
    void setMute (boolean value);


    /**
     * Toggle mute.
     */
    void toggleMute ();


    /**
     * True if soloed.
     *
     * @return True if soloed.
     */
    boolean isSolo ();


    /**
     * Turn on/off solo.
     *
     * @param value True to turn on solo, otherwise off
     */
    void setSolo (boolean value);


    /**
     * Toggle solo.
     */
    void toggleSolo ();


    /**
     * Duplicate the track.
     */
    void duplicate ();


    /**
     * Select the master track.
     */
    void select ();


    /**
     * Make the master track visible (scrolls to the track in the DAW).
     */
    void makeVisible ();


    /**
     * Select the master track and make it visible.
     */
    void selectAndMakeVisible ();


    /**
     * Get the Mono VU value.
     *
     * @return The VU value
     */
    int getVu ();


    /**
     * Get the left VU value.
     *
     * @return The left VU value
     */
    int getVuLeft ();


    /**
     * Get the right VU value.
     *
     * @return The right VU value
     */
    int getVuRight ();


    /**
     * Get the number of sends (of a page).
     *
     * @return The number of sends
     */
    int getNumSends ();


    /**
     * Get the send of a channel.
     *
     * @param sendIndex The index of the send
     * @return The send
     */
    ISend getSend (int sendIndex);


    /**
     * Start the browser to insert a new device at the beginning of the channels device chain.
     */
    void browseToInsertAtStartOfChain ();


    /**
     * Start the browser to insert a new device at the end of the channels device chain.
     */
    void browseToInsertAtEndOfChain ();
}