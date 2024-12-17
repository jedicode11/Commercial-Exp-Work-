export function useLoadMatomoScript (): void {
  const el = document.createElement('script')
  const script = document.createTextNode(`
    /* Matomo Finder-Widget */
    var _paq = window._paq = window._paq || [];
    /* tracker methods like "setCustomDimension" should be called before "trackPageView" */
    _paq.push(["setDocumentTitle", document.domain + "/" + document.title]);
    _paq.push(['trackPageView']);
    _paq.push(['enableLinkTracking']);
    (function() {
      var u="//matomo.dimedis.de/";
      _paq.push(['setTrackerUrl', u+'matomo.php']);
      _paq.push(['setSiteId', '2']);
      var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
      g.async=true; g.src=u+'matomo.js'; s.parentNode.insertBefore(g,s);
    })();
    /* End Matomo Code */
  `)
  el.appendChild(script)
  document.head.appendChild(el)
}
