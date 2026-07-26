# -*- coding: utf-8 -*-
"""
geocoding.py — Conversão de morada/código postal em coordenadas (lat/lon).

Estratégia em cascata, pensada para Portugal:
  1. GeoAPI.pt pelo código postal CP7 (ex: "5000-558") — âncora fiável,
     dados oficiais dos CTT, sem ambiguidade entre concelhos.
  2. Nominatim (OpenStreetMap) com a morada completa + código postal —
     fallback quando a GeoAPI não responde ou não conhece o CP.

Devolve sempre um dict:
  {"ok": True,  "lat": ..., "lon": ..., "source": "geoapi"|"nominatim",
   "precision": "postal_code"|"address"}
  {"ok": False, "error": "..."}

Sem estado, sem base de dados. Usado pelo endpoint POST /geocode do app.py.
"""

import logging
import re

import requests

log = logging.getLogger("amover-routes")

GEOAPI_URL = "https://json.geoapi.pt/cp/{cp}"
NOMINATIM_URL = "https://nominatim.openstreetmap.org/search"
# O Nominatim exige um User-Agent identificável (política de uso).
NOMINATIM_HEADERS = {"User-Agent": "amover-routes-optimizer/1.0"}
TIMEOUT_S = 6

CP7_RE = re.compile(r"^\d{4}-\d{3}$")


def _normalize_cp(postal_code):
    """Aceita '5000-558', '5000558', ' 5000-558 ' -> '5000-558' (ou None)."""
    if not postal_code:
        return None
    s = re.sub(r"\s", "", str(postal_code))
    if re.fullmatch(r"\d{7}", s):
        s = s[:4] + "-" + s[4:]
    return s if CP7_RE.fullmatch(s) else None


def _geoapi_lookup(cp7):
    """GeoAPI.pt: CP7 -> centro do código postal. Devolve (lat, lon) ou None."""
    try:
        r = requests.get(GEOAPI_URL.format(cp=cp7), timeout=TIMEOUT_S)
        if r.status_code != 200:
            return None
        data = r.json()
        # A GeoAPI devolve o centro do CP em vários formatos consoante a
        # versão; tentamos os campos conhecidos, do mais ao menos comum.
        centro = data.get("centro") or data.get("centroide")
        if isinstance(centro, (list, tuple)) and len(centro) >= 2:
            return float(centro[0]), float(centro[1])
        for key_lat, key_lon in (("latitude", "longitude"), ("lat", "lon")):
            if data.get(key_lat) is not None and data.get(key_lon) is not None:
                return float(data[key_lat]), float(data[key_lon])
        # Alguns CPs devolvem uma lista de "partes"/ruas com coordenadas.
        partes = data.get("partes") or data.get("pontos") or []
        for p in partes:
            la = p.get("latitude") or p.get("lat")
            lo = p.get("longitude") or p.get("lon")
            if la is not None and lo is not None:
                return float(la), float(lo)
        return None
    except Exception as e:
        log.warning("GeoAPI falhou para %s: %s", cp7, e)
        return None


def _nominatim_lookup(query):
    """Nominatim: morada -> primeiro resultado. Devolve (lat, lon) ou None."""
    try:
        r = requests.get(
            NOMINATIM_URL,
            params={"q": query, "format": "json", "limit": 1,
                    "countrycodes": "pt"},
            headers=NOMINATIM_HEADERS,
            timeout=TIMEOUT_S,
        )
        if r.status_code != 200:
            return None
        results = r.json()
        if not results:
            return None
        return float(results[0]["lat"]), float(results[0]["lon"])
    except Exception as e:
        log.warning("Nominatim falhou para %r: %s", query, e)
        return None


def geocode(postal_code=None, street=None, city=None, door_number=None):
    """
    Converte código postal (preferido) e/ou morada em coordenadas.
    Pelo menos um de postal_code / street deve ser fornecido.
    """
    cp7 = _normalize_cp(postal_code)

    # 1) Código postal via GeoAPI — a via mais fiável (sem ambiguidade).
    if cp7:
        coords = _geoapi_lookup(cp7)
        if coords:
            return {"ok": True, "lat": coords[0], "lon": coords[1],
                    "source": "geoapi", "precision": "postal_code"}

    # 2) Morada completa via Nominatim — fallback.
    parts = [p for p in (street, door_number, cp7, city or "", "Portugal") if p]
    query = ", ".join(str(p) for p in parts if str(p).strip())
    if street or cp7:
        coords = _nominatim_lookup(query)
        if coords:
            return {"ok": True, "lat": coords[0], "lon": coords[1],
                    "source": "nominatim", "precision": "address"}

    return {"ok": False,
            "error": "Sem resultado: código postal desconhecido e morada "
                     "não encontrada. Verifique os dados da tarefa."}
