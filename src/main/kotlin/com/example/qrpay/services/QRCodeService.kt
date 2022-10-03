package com.example.qrpay.services

import com.example.qrpay.repositories.TransferRepository
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Service

@Service
class QRCodeService(
    private val repository: TransferRepository
) {

    fun generateQrCode(hash: String): FileSystemResource? {
        val intent = repository.findByHash(hash) ?: return null

        val qrCodeFile = QRCode.from(intent.id)
            .to(ImageType.PNG)
            .withSize(250, 250)
            .svg()

        return FileSystemResource(qrCodeFile.path)
    }

}
