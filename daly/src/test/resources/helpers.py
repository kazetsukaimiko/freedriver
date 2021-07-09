import json

def binarry(byteData):
    arry = []
    for byte in byteData:
        arry.append(byte)
    return arry


def binhexarry(byteData):
    arry = []
    for byte in byteData:
        arry.append(hex(byte))
    return arry

"""
Test against: https://github.com/jblance/mpp-solar/blob/master/mppsolar/protocols/protocol_helpers.py#L11
Added in step debugging to clarify what is going wrong where
"""
def crc8(byteData):
    """
    Generate 8 bit CRC of supplied string
    """
    result = {}
    steps = []
    CRC = 0
    # for j in range(0, len(str),2):
    for b in byteData:
        # char = int(str[j:j+2], 16)
        # print(b)
        steps.append({ "start": CRC, "component": b, "end": CRC + b })
        CRC = CRC + b
    CRC &= 0xFF
    result["crc"] = CRC
    result["steps"] = steps
    return result


def jsonprint(obj):
    print(json.dumps(obj))


# Use bytearray([1,2,3,4]) to convert to binary strings

