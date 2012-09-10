

filmstripPanelHTML = """<table cellspacing="0" cellpadding="0">
<tr>
  <td>
    <a href="$BACKLINK">
    <img src="icons/back_64.png" alt="back" width="64" height="64" border="0" />
    </a>
  </td>
  $GALLERYBUTTON
  <td align="center" valign="middle" width="100" height="75">
  $FILM1
  </td>
  <td align="center" valign="middle" width="100" height="75">
  $FILM2
  </td> 
  <td align="center" valign="middle" width="100" height="75">
  $FILM3
  </td>   
  <td>
    <a href="$NEXTLINK">  
    <img src="icons/next_64.png" alt="back" width="64" height="64" border="0" />
    </a>    
  </td>
</tr>
</table>
"""


flimStripGalleryButtonHTML = """<a href="noscript_gallery.html">
<img src="icons/gallery_64.png" alt="back" width="64" height="64" border="0" />
</a>
"""

filmStripLinkHTML = """<a href="$LINK">
  <img src="$IMG" alt="$IMG" class="filmstrip" width="$W" height="$H" />
</a>  
"""

filmStripNoLinkHTML = """<img src="$IMG" alt="$IMG" class="filmstripHighlighted" width="$W" height="$H" />"""
